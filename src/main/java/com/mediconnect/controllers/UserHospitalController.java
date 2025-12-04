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

    // 3. Nearby hospitals within given distance (in km)
    @GetMapping("/nearby")
    public List<HospitalResponseDTO> getNearbyHospitals(
            Authentication authentication,
            @RequestParam double userLat,
            @RequestParam double userLng,
            @RequestParam(defaultValue = "10") double maxDistanceKm) {
        System.out.println("Called /nearby hospitals API");
        System.out.println("Params -> userLat: " + userLat + ", userLng: " + userLng + ", maxDistanceKm: " + maxDistanceKm);
        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        List<HospitalResponseDTO> allHospitals = hospitalService.getAllHospitals();
        System.out.println("Total hospitals in DB: " + allHospitals.size());
        List<HospitalResponseDTO> nearbyHospitals = allHospitals.stream()
                .filter(h -> {
                    double distance = distanceInKm(userLat, userLng, h.getLatitude(), h.getLongitude());
                    return distance <= maxDistanceKm;
                })
                .toList();

        System.out.println("Nearby hospitals found: " + nearbyHospitals.size());
        return nearbyHospitals;
    }


    private double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
