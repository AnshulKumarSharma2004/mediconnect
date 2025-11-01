package com.mediconnect.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorResponseDTO {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String qualification;
    private int experienceYears;
    private String imageUrl;
    private String hospitalId;
    private double rating;
    private boolean active;
    private String description;
    private double consultationFee;
    private double appointmentFee;
}
