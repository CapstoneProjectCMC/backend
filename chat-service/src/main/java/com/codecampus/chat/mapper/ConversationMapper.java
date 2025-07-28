package com.codecampus.chat.mapper;

import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.entity.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface ConversationMapper {
    ConversationResponse toConversationResponseFromConversation(
            Conversation conversation);

    List<ConversationResponse> toListConversationResponseFromListConversation(
            List<Conversation> conversations);
}
