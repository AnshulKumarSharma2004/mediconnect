package com.mediconnect.controllers;

import com.mediconnect.model.ChatMessage;
import com.mediconnect.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
@MessageMapping("/chat.sendMessage")
@SendTo("/topic/room.{roomId}")
    public ChatMessage sendMessage(ChatMessage message){
   message.setTimestamp(LocalDateTime.now());
   chatMessageRepository.save(message);
   return message;
    }
}
