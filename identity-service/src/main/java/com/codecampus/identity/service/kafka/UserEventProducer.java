package com.codecampus.identity.service.kafka;

import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.mapper.kafka.UserPayloadMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventProducer {

    @Value("${app.event.user-events}")
    @NonFinal
    static String USER_EVENTS_TOPIC;

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;
    UserPayloadMapper userPayloadMapper;

    public void publishCreatedUserEvent(
            User user) {
        publishEvent(UserEvent.Type.CREATED, user);
    }

    public void publishUpdatedUserEvent(User user) {
        publishEvent(UserEvent.Type.UPDATED, user);
    }

    public void publishDeletedUserEvent(User user) {
        publishEvent(UserEvent.Type.DELETED, user);
    }

    void publishEvent(
            UserEvent.Type type,
            User user) {
        UserEvent userEvent = UserEvent.builder()
                .type(type)
                .id(user.getId())
                .payload(type == UserEvent.Type.DELETED ? null
                        : userPayloadMapper.toUserPayloadFromUser(
                        user))
                .build();

        try {
            String jsonObject = objectMapper.writeValueAsString(
                    userEvent);

            kafkaTemplate.send(
                    USER_EVENTS_TOPIC,
                    user.getId(),
                    jsonObject);
        } catch (JsonProcessingException exception) {
            log.error("[Kafka] Serialize thất bại", exception);
            throw new RuntimeException(exception);
        }
    }
}
