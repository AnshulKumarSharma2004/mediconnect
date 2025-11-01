package com.mediconnect.controllers;

import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/doctors")
public class UserDoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/filter")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsWithFilter(
            @RequestParam("filter") String filter,
            @RequestParam(value = "hospitalId", required = false) String hospitalId
    ) {
        List<DoctorResponseDTO> doctors;

        switch (filter.toLowerCase()) {
            case "all":
                doctors = doctorService.getAllDoctors();
                break;

            case "top":
                doctors = doctorService.getTopDoctors();
                break;

            case "hospital":
                if (hospitalId == null || hospitalId.isEmpty()) {
                    throw new RuntimeException("hospitalId is required for filter 'hospital'");
                }
                doctors = doctorService.getDoctorsByHospital(hospitalId);
                break;

            default:
                throw new RuntimeException("Invalid filter. Use 'all', 'top', or 'hospital'");
        }

        return ResponseEntity.ok(doctors);
    }
}
