package com.codecampus.chat.service;

import com.codecampus.chat.dto.common.PageResponse;
import com.codecampus.chat.dto.request.ConversationRequest;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ParticipantInfo;
import com.codecampus.chat.exception.AppException;
import com.codecampus.chat.exception.ErrorCode;
import com.codecampus.chat.helper.AuthenticationHelper;
import com.codecampus.chat.helper.ChatMessageHelper;
import com.codecampus.chat.helper.PageResponseHelper;
import com.codecampus.chat.repository.ConversationRepository;
import com.codecampus.chat.service.cache.UserSummaryCacheService;
import dtos.UserProfileSummary;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {

  ConversationRepository conversationRepository;
  ChatMessageHelper chatMessageHelper;
  UserSummaryCacheService cache;

  public PageResponse<ConversationResponse> getMyConversations(
      int page, int size) {
    String userId = AuthenticationHelper.getMyUserId();
    Pageable pageable = PageRequest.of(page - 1, size,
        Sort.by(Sort.Direction.DESC, "modifiedDate"));

    Page<Conversation> pageData =
        conversationRepository
            .findAllByParticipantIdsContains(userId, pageable);
    Page<ConversationResponse> out = pageData.map(
        chatMessageHelper::toConversationResponseFromConversation);

    return PageResponseHelper.toPageResponse(out, page);
  }

  public ConversationResponse createConversation(
      ConversationRequest conversationRequest) {

    // Fetch user infos
    String me = AuthenticationHelper.getMyUserId();
    String you = conversationRequest.participantIds().getFirst();

    UserProfileSummary meS = cache.getOrLoad(me);
    UserProfileSummary youS = cache.getOrLoad(you);

    if (Objects.isNull(meS) || Objects.isNull(youS)) {
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

    List<String> sortedIds =
        Arrays.asList(meS.userId(), youS.userId())
            .stream()
            .sorted()
            .toList();

    String participantsHash =
        chatMessageHelper.generateParticipantHash(sortedIds);

    Conversation conversation = conversationRepository
        .findByParticipantsHash(participantsHash)
        .orElseGet(() -> {
          List<ParticipantInfo> participants =
              List.of(chatMessageHelper.toParticipantInfoFromUserProfileSummary(
                      meS),
                  chatMessageHelper.toParticipantInfoFromUserProfileSummary(
                      youS));
          return conversationRepository.save(
              Conversation.builder()
                  .type(conversationRequest.type())
                  .participantsHash(participantsHash)
                  .createdDate(Instant.now())
                  .modifiedDate(Instant.now())
                  .participants(participants)
                  .build()
          );
        });

    return chatMessageHelper.toConversationResponseFromConversation(
        conversation);
  }
}
