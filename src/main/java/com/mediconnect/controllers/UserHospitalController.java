package com.mediconnect.controllers;

import com.mediconnect.dtos.HospitalResponseDTO;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/hospitals")
@RequiredArgsConstructor
public class UserHospitalController {
@Autowired
    private  HospitalService hospitalService;
@Autowired
private UserRepository userRepository;

    //  1. Top hospitals
    @GetMapping("/top")
    public List<HospitalResponseDTO> getTopHospitals(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return hospitalService.getTopHospitals();
    }


    //  2. All hospitals
    @GetMapping("/all")
    public List<HospitalResponseDTO> getAllHospitals(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return hospitalService.getAllHospitals();
    }
}
