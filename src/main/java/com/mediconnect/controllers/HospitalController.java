package com.mediconnect.controllers;
import com.mediconnect.dtos.DoctorResponseDTO;
import com.mediconnect.dtos.HospitalLoginRequestDTO;
import com.mediconnect.dtos.HospitalResponseDTO;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.Hospital;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.AppointmentService;
import com.mediconnect.services.DoctorService;
import com.mediconnect.services.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/hospitals")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AppointmentService appointmentService;
// create hospital
    @PostMapping(value = "/register-hospital", consumes = {"multipart/form-data"})
    public ResponseEntity<HospitalResponseDTO> createHospital(
            @RequestPart("hospital")Hospital hospital,
            @RequestPart(value = "images",required = false) List<MultipartFile> images,
            Authentication auth){
  String email = auth.getName();
  User user = userRepository.findByEmail(email)
          .orElseThrow(()-> new RuntimeException("User Not Found"));
        HospitalResponseDTO response = hospitalService.createHospital(hospital, images, user.getId().toString());
        return ResponseEntity.ok(response);
    }
    // Get Hospital
    @GetMapping("/get-my-hospital")
    public ResponseEntity<HospitalResponseDTO> getMyHospital(Authentication auth){
   String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        HospitalResponseDTO responseDTO = hospitalService.getHospitalByAdmin(user.getId().toString());
        return ResponseEntity.ok(responseDTO);
    }
    // update Hospital
    @PutMapping(value = "/update/{hospitalId}", consumes = {"multipart/form-data"})
    public ResponseEntity<HospitalResponseDTO> updateHospital(
            @PathVariable String hospitalId,
            @RequestPart("hospital") Hospital updatedHospital,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        HospitalResponseDTO response = hospitalService.updateHospital(hospitalId, updatedHospital, newImages, user.getId().toString());
        return ResponseEntity.ok(response);
    }
// delete Hospital
    @DeleteMapping("delete/{hospitalId}")
    public ResponseEntity<String> deleteHospital(@PathVariable String hospitalId, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        hospitalService.deleteHospitalById(hospitalId, user.getId().toString());
        return ResponseEntity.ok("Hospital and its images deleted successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<HospitalResponseDTO> hospitalLogin(
            Authentication auth,
            @RequestBody HospitalLoginRequestDTO loginRequest) {

        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Admin Not Found"));
        String hospitalEmail = loginRequest.getEmail();
        String regNo = loginRequest.getRegistrationNumber();

        HospitalResponseDTO hospital = hospitalService.authenticateHospital(admin.getId().toString(),hospitalEmail,regNo);

        return ResponseEntity.ok(hospital);
    }
    @GetMapping("/dashboard/counts")
    public ResponseEntity<Map<String, Long>> getDashboardCounts(Authentication auth) {
        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No admin found"));

        // Get hospitalId for this admin
        HospitalResponseDTO hospital = hospitalService.getHospitalByAdmin(admin.getId().toString());
        String hospitalId = hospital.getId();

        long totalDoctors = doctorService.getDoctorsByHospital(hospitalId).size();
        long totalPatients = appointmentService.getTotalPatients(hospitalId);
        long appointmentsToday = appointmentService.getAppointmentsForToday(hospitalId).size();
        long pendingAppointments = appointmentService.getPendingAppointments(hospitalId);

        // 🔹 Debug prints
        System.out.println("Hospital ID: " + hospitalId);
        System.out.println("Total Doctors: " + totalDoctors);
        System.out.println("Total Patients: " + totalPatients);
        System.out.println("Appointments Today: " + appointmentsToday);
        System.out.println("Pending Appointments: " + pendingAppointments);

        Map<String, Long> response = new HashMap<>();
        response.put("totalDoctors", totalDoctors);
        response.put("appointmentsToday", appointmentsToday);
        response.put("totalPatients", totalPatients);
        response.put("pendingAppointments", pendingAppointments);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
