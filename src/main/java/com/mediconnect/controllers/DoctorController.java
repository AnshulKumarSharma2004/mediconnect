package com.mediconnect.controllers;

import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hospital/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    // Add doctor
    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<DoctorResponseDTO> addDoctor(
            @RequestPart("doctor") Doctor doctor,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication auth
    ) {
        User admin = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        DoctorResponseDTO response = doctorService.addDoctor(admin.getId().toString(), doctor, image);
        return ResponseEntity.ok(response);
    }

    // Update doctor
    @PutMapping(value = "/update/{doctorId}", consumes = {"multipart/form-data"})
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable String doctorId,
            @RequestPart("doctor") Doctor doctor,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        DoctorResponseDTO response = doctorService.updateDoctor(doctorId, doctor, image);
        return ResponseEntity.ok(response);
    }

    // Get all doctors for this admin
    @GetMapping("/my-doctors")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctors(Authentication auth) {
        User admin = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsByAdmin(admin.getId().toString());
        return ResponseEntity.ok(doctors);
    }

    // Delete doctor
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<String> deleteDoctor(@PathVariable String doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.ok("Doctor deleted successfully");
    }
}
