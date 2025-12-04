package com.mediconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    private ObjectId id;

    private String doctorId;
    private String patientId;
    private String hospitalId;

    private LocalDateTime appointmentDate;
    private String status;
    private String paymentStatus;
    private  String hospitalName;
    private double appointmentFee;
    private String appointmentType;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String patientName;
    private String doctorName;
    private double rating=0.0;







}
