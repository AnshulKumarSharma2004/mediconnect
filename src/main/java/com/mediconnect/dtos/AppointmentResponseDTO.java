package com.mediconnect.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    private String id;
    private String doctorId;
    private String patientId;
    private String hospitalId;

    private LocalDateTime appointmentDate;
    private String status;
    private String paymentStatus;
    private String hospitalName;

    private double appointmentFee;
    private String appointmentType;
    private String patientName;
    private String doctorName;
    private double rating;

}
