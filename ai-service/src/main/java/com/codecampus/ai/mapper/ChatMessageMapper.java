package com.codecampus.ai.mapper;

import com.codecampus.ai.dto.response.chat.MessageResponse;
import com.codecampus.ai.entity.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

  default MessageResponse toMessageResponseFromChatMessage(
      ChatMessage chatMessage) {
    return new MessageResponse(
        chatMessage.getId(),
        chatMessage.getChatRole().name(),
        chatMessage.getContent(),
        chatMessage.getImageOriginalName(),
        chatMessage.getImageContentType(),
        chatMessage.getImageUrl(),
        chatMessage.getCreatedAt()
    );
  }
}
