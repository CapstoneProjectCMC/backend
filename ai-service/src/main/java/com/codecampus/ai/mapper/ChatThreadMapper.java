package com.codecampus.ai.mapper;

import com.codecampus.ai.dto.response.chat.MessageResponse;
import com.codecampus.ai.dto.response.chat.ThreadDetailResponse;
import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.entity.ChatThread;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatThreadMapper {

    ThreadResponse toThreadResponseFromChatThread(
            ChatThread chatThread);

    default ThreadDetailResponse toThreadDetailResponseFromChatThreadAndMessageResponseList(
            ChatThread chatThread,
            List<MessageResponse> messages) {
        return new ThreadDetailResponse(
                chatThread.getId(),
                chatThread.getTitle(),
                chatThread.getLastMessageAt(),
                chatThread.getCreatedAt(),
                chatThread.getUpdatedAt(),
                messages
        );
    }
}
