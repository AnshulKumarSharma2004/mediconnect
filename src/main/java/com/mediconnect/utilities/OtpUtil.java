package com.mediconnect.utilities;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OtpUtil {

    public static String encryptOTP(String otp){
try{
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] hash = md.digest(otp.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if(hex.length() == 1) hexString.append('0');
        hexString.append(hex);
    }
    return hexString.toString();
} catch (Exception ex) {
    throw new RuntimeException("Error while encrypting OTP", ex);
}

    }
}
