package com.codecampus.chat.helper;

import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.entity.ChatMessage;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ParticipantInfo;
import com.codecampus.chat.mapper.ChatMessageMapper;
import com.codecampus.chat.mapper.ConversationMapper;
import dtos.UserProfileSummary;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
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

    if ("DIRECT".equals(conversation.getType())) {
      conversation.getParticipants().stream()
          .filter(p -> !p.getUserId().equals(currentUserId))
          .findFirst()
          .ifPresent(p -> {
            conversationResponse.setConversationName(p.getDisplayName());
            conversationResponse.setConversationAvatar(p.getAvatarUrl());
          });
    } else { // GROUP
      conversationResponse.setConversationName(
          conversation.getName() != null ? conversation.getName() : "Nhóm");
      conversationResponse.setConversationAvatar(conversation.getAvatarUrl());
      conversationResponse.setTopic(conversation.getTopic());
    }

    return conversationResponse;
  }

  public String generateParticipantHash(List<String> ids) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String stringJoiner = String.join("_", ids);
      byte[] digest =
          md.digest(stringJoiner.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      // fallback an toàn
      return String.join("_", ids);
    }
  }

  public ParticipantInfo toParticipantInfoFromUserProfileSummary(
      UserProfileSummary userProfileSummary) {
    return ParticipantInfo.builder()
        .userId(userProfileSummary.userId())
        .username(userProfileSummary.username())
        .email(userProfileSummary.email())
        .active(userProfileSummary.active())
        .displayName(userProfileSummary.displayName())
        .firstName(userProfileSummary.firstName())
        .lastName(userProfileSummary.lastName())
        .avatarUrl(userProfileSummary.avatarUrl())
        .build();
  }
}
