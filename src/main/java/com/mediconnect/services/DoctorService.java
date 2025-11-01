package com.mediconnect.services;

import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.Hospital;
import com.mediconnect.repositories.DoctorRepository;
import com.mediconnect.repositories.HospitalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ImageUploadService imageUploadService;

    // Add doctor
    public DoctorResponseDTO addDoctor(String adminId, Doctor doctor, MultipartFile image) {
        Hospital hospital = hospitalRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Hospital not found for this admin"));

        doctor.setHospitalId(hospital.getId().toString());
        doctor.setAdminId(adminId);
        doctor.setActive(true);

        // Generate random password
        String rawPassword = "Doc@" + System.currentTimeMillis();
        doctor.setPassword(passwordEncoder.encode(rawPassword));

        // Save first to get doctorId
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Handle image after save
        if (image != null && !image.isEmpty()) {
            String folderName = "hospitals/" + adminId + "/" + hospital.getRegistrationNumber() + "/doctors/" + savedDoctor.getId();
            savedDoctor.setImageUrl(imageUploadService.uploadImage(image, folderName));
            savedDoctor = doctorRepository.save(savedDoctor);
        }

        // Send credentials email
        emailService.sendDoctorCredentialsEmail(
                savedDoctor.getEmail(),
                savedDoctor.getName(),
                rawPassword,
                hospital.getName()
        );

        return convertToDTO(savedDoctor);
    }

    // Update doctor
    public DoctorResponseDTO updateDoctor(String doctorId, Doctor updatedDoctor, MultipartFile newImage) {
        Doctor existing = doctorRepository.findById(new ObjectId(doctorId))
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Update fields
        if (updatedDoctor.getName() != null && !updatedDoctor.getName().isEmpty()) {
            existing.setName(updatedDoctor.getName());
        }

        if (updatedDoctor.getSpecialization() != null && !updatedDoctor.getSpecialization().isEmpty()) {
            existing.setSpecialization(updatedDoctor.getSpecialization());
        }

        if (updatedDoctor.getPhoneNumber() != null && !updatedDoctor.getPhoneNumber().isEmpty()) {
            existing.setPhoneNumber(updatedDoctor.getPhoneNumber());
        }

        if (updatedDoctor.getQualification() != null && !updatedDoctor.getQualification().isEmpty()) {
            existing.setQualification(updatedDoctor.getQualification());
        }

        if (updatedDoctor.getExperienceYears() > 0) {
            existing.setExperienceYears(updatedDoctor.getExperienceYears());
        }
        if (updatedDoctor.getRating() > 0) {
            existing.setRating(updatedDoctor.getRating());
        }
        if (updatedDoctor.getConsultationFee() > 0) {
            existing.setConsultationFee(updatedDoctor.getConsultationFee());
        }

        if (updatedDoctor.getAppointmentFee() > 0) {
            existing.setAppointmentFee(updatedDoctor.getAppointmentFee());
        }

        // Handle new image
        if (newImage != null && !newImage.isEmpty()) {
            if (existing.getImageUrl() != null) {
                imageUploadService.deleteImage(existing.getImageUrl());
            }

            Hospital hospital = hospitalRepository.findByAdminId(existing.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));

            String folderName = "hospitals/" + existing.getAdminId() + "/" +
                    hospital.getRegistrationNumber() + "/doctors/" + doctorId;

            existing.setImageUrl(imageUploadService.uploadImage(newImage, folderName));
        }

        Doctor saved = doctorRepository.save(existing);
        return convertToDTO(saved);
    }

    // Get all doctors by admin
    public List<DoctorResponseDTO> getDoctorsByAdmin(String adminId) {
        Hospital hospital = hospitalRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        return doctorRepository.findByHospitalId(hospital.getId().toString())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Delete doctor
    public void deleteDoctor(String doctorId) {
        Doctor doctor = doctorRepository.findById(new ObjectId(doctorId))
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (doctor.getImageUrl() != null) {
            imageUploadService.deleteImage(doctor.getImageUrl());
        }

        doctorRepository.deleteById(new ObjectId(doctorId));
    }

    private DoctorResponseDTO convertToDTO(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId().toString(),
                doctor.getName(),
                doctor.getEmail(),
                doctor.getPhoneNumber(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getImageUrl(),
                doctor.getHospitalId(),
                doctor.getRating(),
                doctor.isActive(),
                doctor.getDescription(),
                doctor.getConsultationFee(),
                doctor.getAppointmentFee()
        );
    }
    public List<DoctorResponseDTO> getDoctorsByHospital(String hospitalId) {
        List<Doctor> doctors = doctorRepository.findByHospitalIdAndActiveTrue(hospitalId);

        List<DoctorResponseDTO> dtos = doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return dtos;
    }
    public List<DoctorResponseDTO> getTopDoctors() {
        List<Doctor> doctors = doctorRepository.findTop10ByOrderByRatingDesc();
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
