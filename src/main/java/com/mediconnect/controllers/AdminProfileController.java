package com.mediconnect.controllers;

import com.mediconnect.dtos.UpdateDTO;
import com.mediconnect.dtos.UserResponseDTO;
import com.mediconnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profile")
public class AdminProfileController {

    @Autowired
    private UserService userService;

    // Get profile
    @GetMapping
    public ResponseEntity<UserResponseDTO> getProfile(Authentication auth) {
        String email = auth.getName();
        UserResponseDTO response = userService.getProfile(email);
        return ResponseEntity.ok(response);
    }

    // Update profile (partial update)
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateDTO updateDTO,
                                                         Authentication auth) {
        String email = auth.getName();
        UserResponseDTO response = userService.updateProfile(email, updateDTO);
        return ResponseEntity.ok(response);
    }
}
