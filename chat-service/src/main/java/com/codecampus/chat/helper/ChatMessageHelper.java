package com.codecampus.chat.helper;

import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.entity.ChatMessage;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.mapper.ChatMessageMapper;
import com.codecampus.chat.mapper.ConversationMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageHelper {

    ChatMessageMapper chatMessageMapper;
    ConversationMapper conversationMapper;

    public ChatMessageResponse toChatMessageResponseFromChatMessage(
            ChatMessage chatMessage) {

        String userId = AuthenticationHelper.getMyUserId();

        ChatMessageResponse chatMessageResponse = chatMessageMapper
                .toChatMessageResponseFromChatMessage(chatMessage);

        chatMessageResponse.setMe(
                userId.equals(chatMessage.getSender().getUserId())
        );

        return chatMessageResponse;
    }


    public ConversationResponse toConversationResponseFromConversation(
            Conversation conversation) {
        String currentUserId = AuthenticationHelper.getMyUserId();

        ConversationResponse conversationResponse =
                conversationMapper.toConversationResponseFromConversation(
                        conversation);

        conversation.getParticipants()
                .stream()
                .filter(participantInfo -> !participantInfo.getUserId()
                        .equals(currentUserId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(
                            participantInfo.getDisplayName());
                    conversationResponse.setConversationAvatar(
                            participantInfo.getAvatarUrl());
                });

        return conversationResponse;
    }

    public String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        // SHA 256
        return stringJoiner.toString();
    }
}
