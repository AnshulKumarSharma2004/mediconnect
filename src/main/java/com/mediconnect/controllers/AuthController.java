package com.mediconnect.controllers;
import com.mediconnect.dtos.LoginResponse;
import com.mediconnect.dtos.UserResponseDTO;
import com.mediconnect.model.User;
import com.mediconnect.services.EmailService;
import com.mediconnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody User user) {
        UserResponseDTO response = userService.register(user);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);
        emailService.sendOtp(email, otp);
        return "OTP sent successfully to " + email;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = emailService.verifyOtp(email, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }
    }

}
