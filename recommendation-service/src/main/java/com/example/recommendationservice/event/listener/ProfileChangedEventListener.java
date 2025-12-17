package com.example.recommendationservice.event.listener;

import com.example.recommendationservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.profile.ProfileChangedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileChangedEventListener {

    private final EmbeddingService embeddingService;

    @KafkaListener(
        topics = "${kafka.event.topic.profile-changed}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(ProfileChangedEvent event) {
        embeddingService.embedFreelancerProfile(event.freelancerCode());
    }

}
