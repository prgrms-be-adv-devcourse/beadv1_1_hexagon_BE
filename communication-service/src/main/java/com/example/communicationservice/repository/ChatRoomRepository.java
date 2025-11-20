package com.example.communicationservice.repository;

import com.example.communicationservice.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    @Query("{ 'memberCodes': { $all: ?0, $size: ?1 } }")
    boolean existsByMemberCodes(List<String> memberCodes, int size);

    @Query("{ 'memberCodes' : ?0 }")
    Page<ChatRoom> findAllByMemberCode(String memberCode, Pageable pageable);

}
