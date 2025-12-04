package com.mediconnect.controllers;

import com.mediconnect.dtos.AppointmentResponseDTO;
import com.mediconnect.dtos.HospitalResponseDTO;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.AppointmentService;
import com.mediconnect.services.DoctorService;
import com.mediconnect.services.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors/appointments")
public class DoctorAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/pending")
    public List<AppointmentResponseDTO> getPendingAppointments(Authentication auth) {
        String email = auth.getName();
        System.out.println("[INFO] Fetching pending appointments for doctor: " + email);

        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Doctor not found: " + email);
                    return new RuntimeException("Doctor not found");
                });

        List<AppointmentResponseDTO> pendingAppointments = appointmentService
                .getAppointmentsByDoctor(doctor.getId().toString(), auth)
                .stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .collect(Collectors.toList());

        System.out.println("[INFO] Pending appointments count: " + pendingAppointments.size());
        return pendingAppointments;
    }


    @PutMapping("/{appointmentId}/accept")
    public AppointmentResponseDTO acceptAppointment(
            @PathVariable String appointmentId,
            Authentication auth) {

        String email = auth.getName();
        System.out.println("[INFO] Accept request for appointment: " + appointmentId + " by doctor: " + email);

        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Doctor not found: " + email);
                    return new RuntimeException("Doctor not found");
                });

        AppointmentResponseDTO updated = appointmentService.updateAppointmentStatus(appointmentId, "BOOKED");
        System.out.println("[INFO] Appointment accepted: " + appointmentId);
        return updated;
    }

    // ---------------- Reject Appointment ----------------
    @PutMapping("/{appointmentId}/reject")
    public AppointmentResponseDTO rejectAppointment(
            @PathVariable String appointmentId,
            Authentication auth) {

        String email = auth.getName();
        System.out.println("[INFO] Reject request for appointment: " + appointmentId + " by doctor: " + email);

        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Doctor not found: " + email);
                    return new RuntimeException("Doctor not found");
                });

        AppointmentResponseDTO updated = appointmentService.updateAppointmentStatus(appointmentId, "REJECTED");
        System.out.println("[INFO] Appointment rejected: " + appointmentId);
        return updated;
    }

    @GetMapping("/booked")
    public List<AppointmentResponseDTO> getBookedAppointments(Authentication auth) {
        String email = auth.getName();
        System.out.println("[INFO] Fetching booked appointments for doctor: " + email);

        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Doctor not found: " + email);
                    return new RuntimeException("Doctor not found");
                });

        List<AppointmentResponseDTO> bookedAppointments = appointmentService
                .getAppointmentsByDoctor(doctor.getId().toString(), auth)
                .stream()
                .filter(a -> "BOOKED".equals(a.getStatus()))
                .collect(Collectors.toList());

        System.out.println("[INFO] Booked appointments count: " + bookedAppointments.size());
        return bookedAppointments;
    }
    @GetMapping("/dashboard/counts")
    public ResponseEntity<Map<String, Long>> getDashboardCounts(Authentication auth) {
        String email = auth.getName();
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor Not Found"));

        String doctorId = doctor.getId().toString();


        long appointmentsToday = appointmentService.getAppointmentsForTodayByDoctor(doctorId).size();
        long totalUniquePatients = appointmentService.getTotalPatientsByDoctor(doctorId);
        long totalAppointments = appointmentService.getTotalAppointmentsByDoctor(doctorId);

        // 🔹 Debug prints
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Appointments Today: " + appointmentsToday);
        System.out.println("Total Unique Patients: " + totalUniquePatients);
        System.out.println("Total Appointments: " + totalAppointments);

        Map<String, Long> response = new HashMap<>();
        response.put("appointmentsToday", appointmentsToday);
        response.put("totalPatients", totalUniquePatients);
        response.put("totalAppointments", totalAppointments);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
