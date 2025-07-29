package com.codecampus.chat.mapper;

import com.codecampus.chat.dto.request.ChatMessageRequest;
import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface ChatMessageMapper {

    ChatMessageResponse toChatMessageResponseFromChatMessage(
            ChatMessage chatMessage);

    ChatMessage toChatMessageFromChatMessageRequest(
            ChatMessageRequest request);

    List<ChatMessageResponse> toListChatMessageResponsesFromListChatMessage(
            List<ChatMessage> chatMessages);
}
