package com.mediconnect.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "doctors")
public class Doctor {

    @Id
    private ObjectId id;

    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private int experienceYears;
    private String qualification;
    private String imageUrl;       // single image
    private String password;
    private String hospitalId;     // linked hospital
    private String adminId;// added by admin
    private double rating;
    private boolean active = true;
   private String description;
    private double consultationFee;
    private double appointmentFee;
}
