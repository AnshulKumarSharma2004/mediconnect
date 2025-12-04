package com.mediconnect.controllers;

import com.mediconnect.dtos.AppointmentResponseDTO;
import com.mediconnect.model.Appointment;
import com.mediconnect.model.User;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/book")
    public AppointmentResponseDTO bookAppointment(@RequestBody Appointment appointment, Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        return appointmentService.bookAppointment(user.getId().toString(),appointment);
    }
    @GetMapping
    public List<AppointmentResponseDTO> getAllAppointments(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return appointmentService.getAppointmentsByPatient(user.getId().toString());
    }

    @PutMapping("/{appointmentId}/pay")
    public AppointmentResponseDTO payNow(@PathVariable String appointmentId, Authentication auth) {
        // Token se user verify
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: check ki ye appointment is user ka hi hai
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
        if (!appointment.getPatientId().equals(user.getId().toString())) {
            throw new RuntimeException("Unauthorized to pay this appointment");
        }

        return appointmentService.updatePaymentStatus(appointmentId, "PAID");
    }

    @PutMapping("/{appointmentId}/cancel")
    public AppointmentResponseDTO cancelAppointment(@PathVariable String appointmentId, Authentication auth) {
        // Token se user verify
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: check ki ye appointment is user ka hi hai
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
        if (!appointment.getPatientId().equals(user.getId().toString())) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }

        return appointmentService.cancelAppointment(appointmentId);
    }
    @PutMapping("/{appointmentId}/rate")
    public AppointmentResponseDTO rateAppointment(
            @PathVariable String appointmentId,
            @RequestParam double rating,
            Authentication auth) {


        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
        if (!appointment.getPatientId().equals(user.getId().toString())) {
            throw new RuntimeException("Unauthorized to rate this appointment");
        }

        // 3️⃣ Call service to update rating
        return appointmentService.rateAppointment(appointmentId, rating);
    }



}
