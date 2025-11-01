package com.mediconnect.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "otp")
@Data
public class OTP {
    @Id
    private ObjectId id;
    private String email;
    private String otp;
    private Date expiryTime;

}
