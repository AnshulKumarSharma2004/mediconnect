package com.mediconnect.controllers;

import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.services.LikedDoctorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class LikedDoctorsController {
@Autowired
    private  LikedDoctorsService likedDoctorsService;

    // Toggle like/unlike doctor
    @PostMapping("/doctors/{doctorId}/toggle-like")
    public ResponseEntity<String> toggleLikeDoctor(@PathVariable String doctorId, Authentication auth) {
        likedDoctorsService.toggleLikeDoctor(auth.getName(), doctorId);
        return ResponseEntity.ok("Toggled successfully");
    }

    //  Get all liked doctors (from liked_doctors collection)
    @GetMapping("/liked-doctors")
    public ResponseEntity<List<DoctorResponseDTO>> getLikedDoctors(Authentication auth) {
        List<DoctorResponseDTO> likedDoctors = likedDoctorsService.getLikedDoctors(auth.getName());
        return ResponseEntity.ok(likedDoctors);
    }
}
