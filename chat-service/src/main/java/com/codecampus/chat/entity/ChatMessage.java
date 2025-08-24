package com.codecampus.chat.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "chat_message")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_sender_userId",
                def = "{ 'sender.userId': 1 }"
        ),
        @CompoundIndex(
                name = "idx_conversation_created",
                def = "{ 'conversationId': 1, 'createdDate': -1 }"
        )
})
public class ChatMessage {

    @MongoId
    String id;

    @Indexed
    String conversationId;

    String message;

    ParticipantInfo sender;

    @Indexed
    Instant createdDate;
}
