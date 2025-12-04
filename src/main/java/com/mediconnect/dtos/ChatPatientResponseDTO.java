package com.mediconnect.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatPatientResponseDTO {
    private String patientId;
    private String name;
    private String email;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
