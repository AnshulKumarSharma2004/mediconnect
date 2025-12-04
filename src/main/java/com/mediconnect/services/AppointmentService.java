package com.mediconnect.services;

import com.mediconnect.dtos.AppointmentResponseDTO;
import com.mediconnect.model.Appointment;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.Hospital;
import com.mediconnect.model.User;
import com.mediconnect.repositories.AppointmentRepository;
import com.mediconnect.repositories.DoctorRepository;
import com.mediconnect.repositories.HospitalRepository;
import com.mediconnect.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HospitalRepository hospitalRepository;

    private AppointmentResponseDTO toDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId().toHexString(),
                appointment.getDoctorId(),
                appointment.getPatientId(),
                appointment.getHospitalId(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getPaymentStatus(),
                appointment.getHospitalName(),
                appointment.getAppointmentFee(),
                appointment.getAppointmentType(),
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getRating()
        );
    }

    // ---------------- Book Appointment ----------------
    public AppointmentResponseDTO bookAppointment(String userId,Appointment appointment) {
        appointment.setPatientId(userId);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setStatus("PENDING");      // Default status
        appointment.setPaymentStatus("UNPAID"); // Default payment status
        if (appointment.getHospitalId() != null) {
            Hospital hospital = hospitalRepository
                    .findById(new ObjectId(appointment.getHospitalId()))
                    .orElse(null);

            if (hospital != null) {
                appointment.setHospitalName(hospital.getName());
            } else {
                appointment.setHospitalName("Unknown Hospital");
            }

        }
        // doctor name
        if (appointment.getDoctorId() != null) {
            Doctor doctor = doctorRepository
                    .findById(new ObjectId(appointment.getDoctorId()))
                    .orElse(null);

            if (doctor != null) {
                appointment.setDoctorName(doctor.getName());
            } else {
                appointment.setDoctorName("Unknown Doctor");
            }
        }
        // patient name
        User patient = userRepository.findById(new ObjectId(userId))
                .orElse(null);

        if (patient != null) {
            appointment.setPatientName(patient.getName());
        } else {
            appointment.setPatientName("Unknown Patient");
        }
        Appointment saved = appointmentRepository.save(appointment);
        return toDTO(saved);
    }



    // ---------------- Get appointments by doctor ----------------
    public List<AppointmentResponseDTO> getAppointmentsByDoctor(String doctorId, Authentication auth) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }





    // ---------------- Update appointment status ----------------
    public AppointmentResponseDTO updateAppointmentStatus(String appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(new ObjectId(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updated = appointmentRepository.save(appointment);

        return toDTO(updated);
    }

    // ---------------- Update payment status ----------------
    public AppointmentResponseDTO updatePaymentStatus(String appointmentId, String paymentStatus) {
        Appointment appointment = appointmentRepository.findById(new ObjectId(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setPaymentStatus(paymentStatus);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updated = appointmentRepository.save(appointment);

        return toDTO(updated);
    }
    public List<AppointmentResponseDTO> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public AppointmentResponseDTO getAppointmentById(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(new ObjectId(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return toDTO(appointment);
    }
    public AppointmentResponseDTO cancelAppointment(String appointmentId) {
        AppointmentResponseDTO appointment = getAppointmentById(appointmentId);
        if (appointment.getAppointmentDate().isAfter(LocalDateTime.now())) {
            return updateAppointmentStatus(appointmentId, "CANCELLED");
        } else {
            throw new RuntimeException("Cannot cancel past appointments");
        }
    }
    public AppointmentResponseDTO rateAppointment(String appointmentId, double rating) {
        // 1️⃣ Convert appointmentId to ObjectId
        ObjectId appointmentObjId = new ObjectId(appointmentId);

        // 2️⃣ Fetch appointment
        Appointment appointment = appointmentRepository.findById(appointmentObjId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // 3️⃣ Set rating in current appointment
        appointment.setRating(rating);
        appointmentRepository.save(appointment);
        System.out.println("✅ Appointment rated: " + rating);

        // 4️⃣ Fetch doctor
        ObjectId doctorObjId = new ObjectId(appointment.getDoctorId());
        Doctor doctor = doctorRepository.findById(doctorObjId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 5️⃣ Calculate doctor's average rating from all rated appointments
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorIdAndRatingNotNull(doctorObjId.toHexString());
        double doctorAvgRating = doctorAppointments.stream()
                .mapToDouble(Appointment::getRating)
                .average()
                .orElse(0.0);
        doctor.setRating(doctorAvgRating);
        doctorRepository.save(doctor);
        System.out.println("✅ Doctor's new avg rating: " + doctorAvgRating);

        // 6️⃣ Fetch hospital
        ObjectId hospitalObjId = new ObjectId(appointment.getHospitalId());
        Hospital hospital = hospitalRepository.findById(hospitalObjId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // 7️⃣ Calculate hospital's average rating from all doctors in that hospital
        List<Doctor> hospitalDoctors = doctorRepository.findByHospitalId(hospitalObjId.toHexString());
        double hospitalAvgRating = hospitalDoctors.stream()
                .mapToDouble(Doctor::getRating)
                .average()
                .orElse(0.0);
        hospital.setRating(hospitalAvgRating);
        hospitalRepository.save(hospital);
        System.out.println("✅ Hospital's new avg rating: " + hospitalAvgRating);

        // 8️⃣ Return updated appointment DTO
        return toDTO(appointment);
    }


    public List<Appointment> getAppointmentsForToday(String hospitalId) {
        LocalDate today = LocalDate.now();

        // Start and end of the day
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                hospitalId, startOfDay, endOfDay
        );
    }

    public long getPendingAppointments(String hospitalId) {
        List<Appointment> pending = appointmentRepository.findByHospitalIdAndStatus(hospitalId, "BOOKED");
        return pending.size();
    }

    public long getTotalPatients(String hospitalId) {
        List<Appointment> allAppointments = appointmentRepository.findByHospitalId(hospitalId);
        // unique patientIds count
        long uniquePatients = allAppointments.stream()
                .map(Appointment::getPatientId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .count();
        return uniquePatients;
    }
    public List<AppointmentResponseDTO> getAppointmentsForTodayByDoctor(String doctorId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                doctorId, startOfDay, endOfDay
        );

        return appointments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public long getTotalPatientsByDoctor(String doctorId) {
        List<Appointment> allAppointments = appointmentRepository.findByDoctorId(doctorId);

        long uniquePatients = allAppointments.stream()
                .map(Appointment::getPatientId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .count();

        return uniquePatients;
    }
    public long getTotalAppointmentsByDoctor(String doctorId) {
        List<Appointment> allAppointments = appointmentRepository.findByDoctorId(doctorId);
        return allAppointments.size();
    }

    public List<AppointmentResponseDTO> getBookedAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, "BOOKED")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
