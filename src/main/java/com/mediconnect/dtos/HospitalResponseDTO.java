package com.mediconnect.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalResponseDTO {

    private String id;


    private String name;
    private String registrationNumber;
    private String website;
    private String email;
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;


    private List<String> departments;
    private List<String> services;
    private List<String> facilities;
    private boolean emergencyAvailable;

    private int totalBeds;
    private int availableBeds;
    private boolean open24Hours;
    private String openingTime;
    private String closingTime;


    private List<String> images;

    private double rating;


    private String upiId;
}
