package com.mediconnect.repositories;

import com.mediconnect.model.OTP;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OTPRepository extends MongoRepository<OTP, ObjectId>{
    Optional<OTP> findByEmail(String email);
}
