package com.mediconnect.repositories;

import com.mediconnect.model.LikedDoctors;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedDoctorsRepository extends MongoRepository<LikedDoctors, String> {
    Optional<LikedDoctors> findByUserId(String userId);
}
