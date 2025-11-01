package com.mediconnect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {
    @Id
    private ObjectId id;

    // ---------------- Basic Info ----------------
    private String name;
    @Indexed(unique = true)
    private String registrationNumber;
    private String website;
    @Indexed(unique = true)
    private String email;
    private String phoneNumber;

    // ---------------- Address & Location ----------------
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private double latitude;
    private double longitude;

    // ---------------- Departments & Services ----------------
    private List<String> departments;
    private List<String> services;
    private List<String> facilities;
    private boolean emergencyAvailable;

    // ---------------- Capacity ----------------
    private int totalBeds;
    private int availableBeds;
    private boolean open24Hours;
    private String openingTime;
    private String closingTime;

    // ---------------- Media ----------------
    private List<String> images;

    // ---------------- Ratings & Reviews ----------------
    private double rating;

    // ---------------- Payments ----------------
    private String upiId;

    // ---------------- Admin & Metadata ----------------
    private String adminId;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;

    private String type;

}
