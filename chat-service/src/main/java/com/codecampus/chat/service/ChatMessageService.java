package com.codecampus.chat.service;

import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.request.ChatMessageRequest;
import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.dto.response.UserProfileResponse;
import com.codecampus.chat.entity.ChatMessage;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ParticipantInfo;
import com.codecampus.chat.entity.WebSocketSession;
import com.codecampus.chat.exception.AppException;
import com.codecampus.chat.exception.ErrorCode;
import com.codecampus.chat.helper.AuthenticationHelper;
import com.codecampus.chat.helper.ChatMessageHelper;
import com.codecampus.chat.mapper.ChatMessageMapper;
import com.codecampus.chat.repository.ChatMessageRepository;
import com.codecampus.chat.repository.ConversationRepository;
import com.codecampus.chat.repository.WebSocketSessionRepository;
import com.codecampus.chat.repository.httpClient.ProfileClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {

    SocketIOServer socketIOServer;

    ChatMessageRepository chatMessageRepository;
    ConversationRepository conversationRepository;
    WebSocketSessionRepository webSocketSessionRepository;
    ProfileClient profileClient;

    ObjectMapper objectMapper;
    ChatMessageMapper chatMessageMapper;

    ChatMessageHelper chatMessageHelper;

    public List<ChatMessageResponse> getChatMessages(
            String conversationId) {

        // Validate conversationId
        String userId = AuthenticationHelper.getMyUserId();

        conversationRepository
                .findById(conversationId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND)
                )
                .getParticipants()
                .stream()
                .filter(
                        participantInfo -> userId.equals(
                                participantInfo.getUserId())
                )
                .findAny()
                .orElseThrow(
                        () -> new AppException(
                                ErrorCode.CONVERSATION_NOT_FOUND)
                );

        List<ChatMessage> chatMessages =
                chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(
                        conversationId);

        return chatMessages.stream()
                .map(chatMessageHelper::toChatMessageResponseFromChatMessage)
                .toList();
    }

    public ChatMessageResponse createChatMessage(
            ChatMessageRequest chatMessageRequest)
            throws JsonProcessingException {

        String userId = AuthenticationHelper.getMyUserId();

        // Validate conversationId
        Conversation conversation = conversationRepository
                .findById(chatMessageRequest.conversationId())
                .orElseThrow(() -> new AppException(
                        ErrorCode.CONVERSATION_NOT_FOUND));

        conversation.getParticipants().stream()
                .filter(
                        participantInfo -> userId.equals(
                                participantInfo.getUserId())
                )
                .findAny()
                .orElseThrow(
                        () -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND)
                );

        // Get UserInfo from Profile Service
        ApiResponse<UserProfileResponse> userResponse =
                profileClient.getUserProfileByUserId(userId);

        if (Objects.isNull(userResponse)) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        UserProfileResponse userInfo = userResponse.getResult();

        // Build Chat message Info
        ChatMessage chatMessage =
                chatMessageMapper.toChatMessageFromChatMessageRequest(
                        chatMessageRequest);
        chatMessage.setSender(ParticipantInfo.builder()
                .userId(userInfo.getUserId())
                .displayName(userInfo.getDisplayName())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .avatarUrl(userInfo.getAvatarUrl())
                .backgroundUrl(userInfo.getBackgroundUrl())
                .build());
        chatMessage.setCreatedDate(Instant.now());

        // Create chat message
        chatMessage = chatMessageRepository.save(chatMessage);

        // Publish socket event to clients is conversation
        // Get participants userIds;
        List<String> userIds = conversation
                .getParticipants()
                .stream()
                .map(ParticipantInfo::getUserId)
                .toList();

        Map<String, WebSocketSession> webSocketSessions =
                webSocketSessionRepository.findAllByUserIdIn(userIds)
                        .stream()
                        .collect(Collectors.toMap(
                                WebSocketSession::getSocketSessionId,
                                Function.identity()));

        ChatMessageResponse chatMessageResponse =
                chatMessageMapper.toChatMessageResponseFromChatMessage(
                        chatMessage);
        socketIOServer.getAllClients().forEach(
                socketIOClient -> {
                    WebSocketSession wsSession = webSocketSessions.get(
                            socketIOClient.getSessionId().toString());

                    if (Objects.nonNull(wsSession)) {
                        String message;

                        try {
                            chatMessageResponse.setMe(
                                    wsSession.getUserId().equals(userId));
                            message = objectMapper.writeValueAsString(
                                    chatMessageResponse);
                            socketIOClient.sendEvent("message", message);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );

        // Convert to Response
        return chatMessageHelper
                .toChatMessageResponseFromChatMessage(chatMessage);
    }
}
