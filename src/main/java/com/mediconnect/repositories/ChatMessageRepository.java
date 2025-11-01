package com.mediconnect.repositories;

import com.mediconnect.model.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage,ObjectId> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
}
