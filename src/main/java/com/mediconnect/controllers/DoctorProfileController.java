package com.mediconnect.controllers;

import com.mediconnect.dtos.ChangePasswordDTO;
import com.mediconnect.dtos.UpdateDTO;
import com.mediconnect.dtos.UserResponseDTO;
import com.mediconnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorProfileController {

    @Autowired
    private UserService userService;

    // Get profile
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication auth) {
        String email = auth.getName();
        UserResponseDTO response = userService.getProfile(email);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateDTO updateDTO,
                                                         Authentication auth) {
        String email = auth.getName();
        UserResponseDTO response = userService.updateProfile(email, updateDTO);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO dto,
                                                 Authentication auth) {
        String email = auth.getName();
        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }
    }
}
