package com.mediconnect.repositories;

import com.mediconnect.model.Appointment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, ObjectId> {

    //  appointments of doctor
    List<Appointment> findByDoctorId(String doctorId);

    //  appointments of patients
    List<Appointment> findByPatientId(String patientId);

    //  appointments of hospital
    List<Appointment> findByHospitalId(String hospitalId);

    //  appointments for particular dates
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            String doctorId, LocalDateTime start, LocalDateTime end);

    // Status filter
    List<Appointment> findByDoctorIdAndStatus(String doctorId, String status);
    List<Appointment> findByDoctorIdAndRatingNotNull(String doctorId);
    List<Appointment> findByHospitalIdAndPatientId(String hospitalId, String patientId);
    List<Appointment> findByHospitalIdAndStatus(String hospitalId, String status);


}
