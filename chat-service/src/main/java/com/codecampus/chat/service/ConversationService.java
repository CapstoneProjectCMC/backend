package com.codecampus.chat.service;

import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.request.ConversationRequest;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.dto.response.UserProfileResponse;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ParticipantInfo;
import com.codecampus.chat.exception.AppException;
import com.codecampus.chat.exception.ErrorCode;
import com.codecampus.chat.helper.AuthenticationHelper;
import com.codecampus.chat.helper.ChatMessageHelper;
import com.codecampus.chat.mapper.ConversationMapper;
import com.codecampus.chat.repository.ConversationRepository;
import com.codecampus.chat.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {

    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    ChatMessageHelper chatMessageHelper;

    public List<ConversationResponse> getMyConversations() {
        String userId = AuthenticationHelper.getMyUserId();
        List<Conversation> conversations =
                conversationRepository
                        .findAllByParticipantIdsContains(userId);

        return conversations.stream()
                .map(chatMessageHelper::toConversationResponseFromConversation)
                .toList();
    }

    public ConversationResponse createConversation(
            ConversationRequest conversationRequest) {

        // Fetch user infos
        String userId = AuthenticationHelper.getMyUserId();
        ApiResponse<UserProfileResponse> userInfoResponse =
                profileClient.getUserProfileByUserId(userId);

        ApiResponse<UserProfileResponse> participantInfoResponse =
                profileClient.getUserProfileByUserId(
                        conversationRequest.participantIds().getFirst());

        if (Objects.isNull(userInfoResponse) ||
                Objects.isNull(participantInfoResponse)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        UserProfileResponse userInfo = userInfoResponse.getResult();
        UserProfileResponse participantInfo =
                participantInfoResponse.getResult();

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfo.getUserId());

        List<String> sortedIds = userIds.stream()
                .sorted()
                .toList();
        String userIdHash =
                chatMessageHelper.generateParticipantHash(sortedIds);

        Conversation conversation = conversationRepository
                .findByParticipantsHash(userIdHash)
                .orElseGet(() -> {
                    List<ParticipantInfo> participantInfos =
                            List.of(ParticipantInfo.builder()
                                            .userId(userInfo.getUserId())
                                            .displayName(userInfo.getDisplayName())
                                            .firstName(userInfo.getFirstName())
                                            .lastName(userInfo.getLastName())
                                            .avatarUrl(userInfo.getAvatarUrl())
                                            .backgroundUrl(userInfo.getBackgroundUrl())
                                            .build(),
                                    ParticipantInfo.builder()
                                            .userId(userInfo.getUserId())
                                            .displayName(
                                                    userInfo.getDisplayName())
                                            .firstName(userInfo.getFirstName())
                                            .lastName(userInfo.getLastName())
                                            .avatarUrl(userInfo.getAvatarUrl())
                                            .backgroundUrl(
                                                    userInfo.getBackgroundUrl())
                                            .build());
                    // Build conversation info
                    Conversation newConversation = Conversation.builder()
                            .type(conversationRequest.type())
                            .participantsHash(userIdHash)
                            .createdDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .build();

                    return conversationRepository.save(newConversation);
                });
        return chatMessageHelper
                .toConversationResponseFromConversation(conversation);
    }
}
