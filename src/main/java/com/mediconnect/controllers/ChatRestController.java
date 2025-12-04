package com.mediconnect.controllers;

import com.mediconnect.dtos.ChatPatientResponseDTO;
import com.mediconnect.dtos.UserResponseDTO;
import com.mediconnect.model.ChatMessage;
import com.mediconnect.model.User;
import com.mediconnect.repositories.ChatMessageRepository;
import com.mediconnect.repositories.UserRepository;
import com.mediconnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    // Get all patients who had chat with this doctor
    @GetMapping("/patients-with-last-message/")
    public List<ChatPatientResponseDTO> getPatientsWithLastMessage(Authentication auth) {
        // Fetch doctor from authentication
        String email = auth.getName();
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new RuntimeException("Unauthorized: Only doctors can access messages");
        }

        String doctorId = doctor.getId().toString();
        System.out.println("🔹 Doctor ID: " + doctorId);

        // Fetch messages involving this doctor
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdOrReceiverId(doctorId, doctorId);
        System.out.println("🔹 Total messages fetched: " + messages.size());
        messages.forEach(m -> System.out.println(
                "Msg: " + m.getSenderId() + " -> " + m.getReceiverId() + " | " + m.getMessage() + " | " + m.getTimestamp()
        ));

        // Get last message per patient
        Map<String, ChatMessage> lastMessageMap = messages.stream()
                .collect(Collectors.toMap(
                        msg -> msg.getSenderId().equals(doctorId) ? msg.getReceiverId() : msg.getSenderId(),
                        msg -> msg,
                        (oldMsg, newMsg) -> newMsg.getTimestamp().isAfter(oldMsg.getTimestamp()) ? newMsg : oldMsg
                ));

        System.out.println("🔹 Last message map keys (patient IDs): " + lastMessageMap.keySet());

        // Map to ChatPatientResponseDTO
        List<ChatPatientResponseDTO> result = new ArrayList<>();
        for (Map.Entry<String, ChatMessage> entry : lastMessageMap.entrySet()) {
            String patientId = entry.getKey();
            ChatMessage lastMsg = entry.getValue();

            try {
                // Fetch patient details from userService
                UserResponseDTO patient = userService.getUserById(patientId);
                if (patient == null) {
                    System.out.println("⚠️ Patient not found for ID: " + patientId);
                    continue;
                }

                ChatPatientResponseDTO dto = new ChatPatientResponseDTO(
                        patient.getId(),
                        patient.getName(),
                        patient.getEmail(),
                        lastMsg.getMessage(),
                        lastMsg.getTimestamp()
                );
                result.add(dto);
                System.out.println("✅ Added patient: " + patient.getName() + " | Last message: " + lastMsg.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Error fetching patient with ID " + patientId + ": " + e.getMessage());
            }
        }

        // Sort by last message time descending
        result.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));

        System.out.println("🔹 Total patients returned: " + result.size());
        return result;
    }

    // Fetch all messages for a room (doctor + patient)
    @GetMapping("/messages/{roomId}")
    public List<ChatMessage> getMessagesByRoom(@PathVariable String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        System.out.println("🔹 Messages for room " + roomId + ": " + messages.size());
        return messages;
    }
}
