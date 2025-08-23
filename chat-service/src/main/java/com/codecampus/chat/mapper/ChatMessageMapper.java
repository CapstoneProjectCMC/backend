package com.codecampus.chat.mapper;

import com.codecampus.chat.dto.request.ChatMessageRequest;
import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "me", ignore = true)
    ChatMessageResponse toChatMessageResponseFromChatMessage(
            ChatMessage chatMessage);

    ChatMessage toChatMessageFromChatMessageRequest(
            ChatMessageRequest request);
}
