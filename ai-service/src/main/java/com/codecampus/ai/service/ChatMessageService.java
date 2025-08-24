package com.codecampus.ai.service;

import com.codecampus.ai.constant.chat.ChatRole;
import com.codecampus.ai.dto.response.chat.MessageResponse;
import com.codecampus.ai.entity.ChatMessage;
import com.codecampus.ai.entity.ChatThread;
import com.codecampus.ai.exception.AppException;
import com.codecampus.ai.exception.ErrorCode;
import com.codecampus.ai.helper.AuthenticationHelper;
import com.codecampus.ai.mapper.ChatMessageMapper;
import com.codecampus.ai.repository.ChatMessageRepository;
import com.codecampus.ai.repository.ChatThreadRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {

  ChatThreadRepository chatThreadRepository;
  ChatMessageRepository chatMessageRepository;
  ChatMessageMapper chatMessageMapper;

  ChatThread getAndValidateThread(String threadId) {
    String userId = AuthenticationHelper.getMyUserId();
    return chatThreadRepository
        .findByIdAndUserId(threadId, userId)
        .orElseThrow(() -> new AppException(
            ErrorCode.CHAT_THREAD_NOT_FOUND));
  }

  @Transactional
  public void addUserMessage(
      String threadId,
      String content) {
    ChatThread thread = getAndValidateThread(threadId);
    ChatMessage chatMessage = ChatMessage.builder()
        .thread(thread)
        .chatRole(ChatRole.USER)
        .content(content == null ? "" : content)
        .build();

    chatMessageMapper.toMessageResponseFromChatMessage(
        chatMessageRepository.save(chatMessage));
  }

  @Transactional
  public void addUserMessageWithImage(
      String threadId,
      String content,
      String originalName,
      String contentType,
      String publicUrl) {
    ChatThread thread = getAndValidateThread(threadId);
    ChatMessage chatMessage = ChatMessage.builder()
        .thread(thread)
        .chatRole(ChatRole.USER)
        .content(content == null ? "" : content)
        .imageOriginalName(originalName)
        .imageContentType(contentType)
        .imageUrl(publicUrl)
        .build();
    chatMessageMapper.toMessageResponseFromChatMessage(
        chatMessageRepository.save(chatMessage));
  }

  @Transactional
  public void addAssistantMessage(
      String threadId,
      String content) {
    ChatThread thread = getAndValidateThread(threadId);
    ChatMessage msg = ChatMessage.builder()
        .thread(thread)
        .chatRole(ChatRole.ASSISTANT)
        .content(content == null ? "" : content)
        .build();
    chatMessageMapper.toMessageResponseFromChatMessage(
        chatMessageRepository.save(msg));
  }

  @Transactional(readOnly = true)
  public List<MessageResponse> listMessages(String threadId) {
    ChatThread thread = getAndValidateThread(threadId);
    return chatMessageRepository
        .findByThreadIdOrderByCreatedAtAsc(
            thread.getId())
        .stream()
        .map(chatMessageMapper::toMessageResponseFromChatMessage)
        .toList();
  }
}
