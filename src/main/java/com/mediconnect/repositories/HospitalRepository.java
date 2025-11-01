package com.mediconnect.repositories;

import com.mediconnect.model.Hospital;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends MongoRepository<Hospital, ObjectId> {
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByEmail(String email);
    Optional<Hospital> findByAdminId(String adminId);

    List<Hospital> findTop10ByOrderByRatingDesc();


    Optional<Hospital> findByEmailAndRegistrationNumber(String email, String regNo);
}
