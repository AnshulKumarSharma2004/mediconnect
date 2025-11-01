package com.mediconnect.services;

import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.LikedDoctors;
import com.mediconnect.model.User;
import com.mediconnect.repositories.DoctorRepository;
import com.mediconnect.repositories.LikedDoctorsRepository;
import com.mediconnect.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikedDoctorsService {
@Autowired
    private  UserRepository userRepository;
    @Autowired
    private  DoctorRepository doctorRepository;
    @Autowired
    private  LikedDoctorsRepository likedDoctorsRepository;

    // Toggle like/unlike doctor
    public void toggleLikeDoctor(String email, String doctorId) {
        // Step 1: Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Validate doctor exists
        ObjectId id = new ObjectId(doctorId);
        doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Step 3: Find or create user's likedDoctors record
        LikedDoctors liked = likedDoctorsRepository.findByUserId(user.getId().toHexString())
                .orElseGet(() -> {
                    LikedDoctors newLiked = new LikedDoctors();
                    newLiked.setUserId(user.getId().toHexString());
                    return likedDoctorsRepository.save(newLiked);
                });

        // Step 4: Toggle logic
        if (liked.getDoctorIds().contains(doctorId)) {
            liked.getDoctorIds().remove(doctorId); // unlike
        } else {
            liked.getDoctorIds().add(doctorId); // like
        }

        // Step 5: Save updated liked list
        likedDoctorsRepository.save(liked);
    }

    //  Return list of liked doctors (by IDs)
    public List<DoctorResponseDTO> getLikedDoctors(String email) {
        // Step 1: Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Get liked record
        Optional<LikedDoctors> likedOpt = likedDoctorsRepository.findByUserId(user.getId().toHexString());
        if (likedOpt.isEmpty() || likedOpt.get().getDoctorIds().isEmpty()) {
            return Collections.emptyList();
        }

        // Step 3: Convert string IDs → ObjectIds
        List<ObjectId> objectIds = likedOpt.get().getDoctorIds()
                .stream()
                .map(ObjectId::new)
                .toList();

        // Step 4: Fetch all doctor details
        List<Doctor> doctors = doctorRepository.findAllById(objectIds);

        // Step 5: Convert to DoctorResponseDTO
        return doctors.stream()
                .map(doc -> {
                    DoctorResponseDTO dto = new DoctorResponseDTO(
                            doc.getId().toHexString(),
                            doc.getName(),
                            doc.getEmail(),
                            doc.getPhoneNumber(),
                            doc.getSpecialization(),
                            doc.getQualification(),
                            doc.getExperienceYears(),
                            doc.getImageUrl(),
                            doc.getHospitalId(),
                            doc.getRating(),
                            doc.isActive(),
                            doc.getDescription(),
                            doc.getConsultationFee(),
                            doc.getAppointmentFee()
                    );
                    return dto;
                })
                .toList();
    }
}
