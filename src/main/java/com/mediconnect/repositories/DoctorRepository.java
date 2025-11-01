package com.mediconnect.repositories;

import com.mediconnect.model.Doctor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, ObjectId> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findByHospitalId(String hospitalId);
    List<Doctor> findByHospitalIdAndActiveTrue(String hospitalId);

    List<Doctor> findTop10ByOrderByRatingDesc();
}
