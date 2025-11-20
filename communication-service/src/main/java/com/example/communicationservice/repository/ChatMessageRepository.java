package com.example.communicationservice.repository;

import com.example.communicationservice.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("{ 'roomId' : ?0 }")
    Page<ChatMessage> findAllByRoomId(String roomId, Pageable pageable);

}
