package com.codecampus.ai.mapper;

import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.entity.ChatThread;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatThreadMapper {

    ThreadResponse toThreadResponseFromChatThread(
            ChatThread chatThread);
}
