package com.mediconnect.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalLoginRequestDTO {
    private String registrationNumber;
    private String email;
}
