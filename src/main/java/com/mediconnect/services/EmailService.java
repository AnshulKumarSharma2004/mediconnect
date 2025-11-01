package com.mediconnect.services;

import com.mediconnect.model.OTP;
import com.mediconnect.repositories.OTPRepository;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.utilities.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OTPRepository otpRepository;
    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendOtp(String toEmail,String otp){
      // saved otp
        OTP otpEntity = new OTP();
        otpEntity.setEmail(toEmail);
        otpEntity.setOtp(OtpUtil.encryptOTP(otp));
        otpEntity.setExpiryTime(new Date(System.currentTimeMillis()+ 5 * 60 *1000));
        otpRepository.save(otpEntity);
        // send to mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your Otp is: "+otp);
        mailSender.send(message);

    }

    public boolean verifyOtp(String email, String otp) {
        return otpRepository.findByEmail(email)
                .map(record -> {
                    // Check if OTP is expired
                    if (record.getExpiryTime().before(new Date())) {
                        return false;
                    }

                    // Check if OTP matches
                    if (record.getOtp().equals(OtpUtil.encryptOTP(otp))) {
                        // Update user verified status
                        userRepository.findByEmail(email).ifPresent(user -> {
                            user.setVerified(true);
                            userRepository.save(user);
                        });

                        // Delete OTP record after verification
                        otpRepository.delete(record);

                        return true;
                    } else {
                        return false;
                    }
                })
                .orElse(false);
    }
    // send Doctor credentials email
    public void sendDoctorCredentialsEmail(String toEmail, String doctorName, String password, String hospitalName){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to "+hospitalName + " | MediConnect Doctor Access");
        message.setText(
                "Hello Dr. " + doctorName + ",\n\n" +
                        "You have been added as a doctor in " + hospitalName + " via MediConnect.\n\n" +
                        "Your temporary login credentials are:\n" +
                        "Email: " + toEmail + "\n" +
                        "Password: " + password + "\n\n" +
                        "Please log in and change your password after first login for security reasons.\n\n" +
                        "Best regards,\n" +
                        "MediConnect Team"
        );
        mailSender.send(message);

    }
}
