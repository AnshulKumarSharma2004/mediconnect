package com.mediconnect.services;

import com.mediconnect.dtos.UpdateDTO;
import com.mediconnect.dtos.UserResponseDTO;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.utilities.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    public UserResponseDTO register(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("PATIENT");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your OTP before login");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getId().toString(),
                user.getName(),
                user.getRole()
        );
    }

    private UserResponseDTO convertToDto(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId().toString());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPhoneNo(user.getPhoneNo());
        response.setVerified(user.isVerified());
        return response;
    }

    public UserResponseDTO getProfile(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return  convertToDto(user);
    }
    public UserResponseDTO updateProfile(String email, UpdateDTO updateDTO){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(updateDTO.getName() != null) user.setName(updateDTO.getName());
        if(updateDTO.getPhoneNo() != null) user.setPhoneNo(updateDTO.getPhoneNo());

        userRepository.save(user);
        return convertToDto(user);
    }

}
