package com.codecampus.chat.service;

import com.codecampus.chat.dto.common.PageResponse;
import com.codecampus.chat.dto.events.ConversationMetaDiff;
import com.codecampus.chat.dto.events.MemberLeftData;
import com.codecampus.chat.dto.events.MembersChangedData;
import com.codecampus.chat.dto.events.MessageReadData;
import com.codecampus.chat.dto.events.MuteData;
import com.codecampus.chat.dto.events.RoleChangeData;
import com.codecampus.chat.dto.request.ConversationRequest;
import com.codecampus.chat.dto.request.GroupUpdateRequest;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.entity.ChatMessage;
import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ConversationMember;
import com.codecampus.chat.entity.ParticipantInfo;
import com.codecampus.chat.exception.AppException;
import com.codecampus.chat.exception.ErrorCode;
import com.codecampus.chat.helper.AuthenticationHelper;
import com.codecampus.chat.helper.ChatMessageHelper;
import com.codecampus.chat.helper.ConversationHelper;
import com.codecampus.chat.helper.EventHelper;
import com.codecampus.chat.helper.PageResponseHelper;
import com.codecampus.chat.helper.WebSocketHelper;
import com.codecampus.chat.repository.ChatMessageRepository;
import com.codecampus.chat.repository.ConversationMemberRepository;
import com.codecampus.chat.repository.ConversationRepository;
import com.codecampus.chat.service.cache.UserProfileSummaryCacheService;
import dtos.UserProfileSummary;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
  ConversationMemberRepository memberRepository;
  ChatMessageRepository chatMessageRepository;
  ChatMessageHelper chatMessageHelper;
  ConversationHelper conversationHelper;
  EventHelper eventHelper;
  UserProfileSummaryCacheService cache;
  WebSocketHelper push;

  public ConversationResponse getConversationById(String conversationId) {
    String me = AuthenticationHelper.getMyUserId();

    Conversation conversation = conversationRepository.findById(conversationId)
        .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

    if (Boolean.TRUE.equals(conversation.getDeleted())) {
      throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
    }

    conversation.getParticipants().stream()
        .filter(p -> me.equals(p.getUserId()))
        .findFirst()
        .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

    ConversationResponse response =
        chatMessageHelper.toConversationResponseFromConversation(conversation);

    ConversationMember st =
        memberRepository.findByConversationIdAndUserId(conversationId, me)
            .orElse(null);

    response.setTopic(conversation.getTopic());
    response.setOwnerId(conversation.getOwnerId());
    response.setAdminIds(conversation.getAdminIds());

    if (st != null) {
      response.setMutedUntil(st.getMutedUntil());
      response.setMyRole(st.getRole());

      Instant lastReadAt = st.getLastReadAt();
      long unread = (lastReadAt == null)
          ? chatMessageRepository.countByConversationId(conversation.getId())
          : chatMessageRepository.countByConversationIdAndCreatedDateAfter(
          conversation.getId(), lastReadAt);
      response.setUnreadCount(unread);
    }
    return response;
  }

  public PageResponse<ConversationResponse> getMyConversations(
      int page, int size) {
    String userId = AuthenticationHelper.getMyUserId();
    Pageable pageable = PageRequest.of(page - 1, size,
        Sort.by(Sort.Direction.DESC, "modifiedDate"));

    Page<Conversation> pageData =
        conversationRepository
            .findAllActiveByParticipantIdsContains(userId, pageable);

    // lấy trạng thái theo user để tính unreadCount
    Map<String, ConversationMember> stateMap =
        memberRepository.findAllByUserId(userId).stream()
            .collect(Collectors.toMap(ConversationMember::getConversationId,
                m -> m));

    Page<ConversationResponse> out = pageData.map(conv -> {
      ConversationResponse r =
          chatMessageHelper.toConversationResponseFromConversation(conv);
      ConversationMember st = stateMap.get(conv.getId());
      r.setTopic(conv.getTopic());
      r.setOwnerId(conv.getOwnerId());
      r.setAdminIds(conv.getAdminIds());
      if (st != null) {
        r.setMutedUntil(st.getMutedUntil());
        r.setMyRole(st.getRole());
        // tính unreadCount giản lược theo lastReadAt vs createdDate của message mới
        Instant lastReadAt = st.getLastReadAt();
        long unread = (lastReadAt == null)
            ? chatMessageRepository.countByConversationId(conv.getId())
            : chatMessageRepository.countByConversationIdAndCreatedDateAfter(
            conv.getId(), lastReadAt);
        r.setUnreadCount(unread);
      }
      return r;
    });

    return PageResponseHelper.toPageResponse(out, page);
  }

  // === TẠO DIRECT ===
  public void createConversation(
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

    var envelope = eventHelper.baseEnvelope(
            "conversation_created",
            conversation, me)
        .data(null)
        .build();

    push.pushToUsers(
        conversation.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "conversation_created",
        envelope
    );
  }

  // === TẠO GROUP ===
  public void createGroup(
      ConversationRequest req,
      GroupUpdateRequest meta) {
    String me = AuthenticationHelper.getMyUserId();
    if (req.participantIds() == null || req.participantIds().isEmpty()) {
      throw new AppException(ErrorCode.PARTICIPANT_NULL);
    }

    Set<String> ids = new HashSet<>(req.participantIds());
    ids.add(me); // đảm bảo chủ group có trong danh sách

    // load profiles
    List<UserProfileSummary> summaries = ids.stream()
        .map(cache::getOrLoad).toList();
    if (summaries.stream().anyMatch(Objects::isNull)) {
      throw new AppException(ErrorCode.FAILED_LOAD_USER);
    }

    List<ParticipantInfo> participants = summaries
        .stream()
        .map(chatMessageHelper::toParticipantInfoFromUserProfileSummary)
        .toList();

    Conversation conv = Conversation.builder()
        .type("GROUP")
        .participants(participants)
        .name(meta != null ? meta.getName() : null)
        .avatarUrl(meta != null ? meta.getAvatarUrl() : null)
        .topic(meta != null ? meta.getTopic() : null)
        .ownerId(me)
        .adminIds(new ArrayList<>())
        .createdDate(Instant.now())
        .modifiedDate(Instant.now())
        .deleted(false)
        .build();
    conv = conversationRepository.save(conv);

    // tạo ConversationMember cho từng thành viên
    Instant now = Instant.now();
    for (String uid : ids) {
      String role = uid.equals(me) ? "OWNER" : "MEMBER";
      memberRepository.save(ConversationMember.builder()
          .conversationId(conv.getId())
          .userId(uid)
          .role(role)
          .joinedAt(now)
          .unreadCount(0L)
          .build());
    }

    // push sự kiện
    var envelope = eventHelper.baseEnvelope(
            "conversation_created", conv, me)
        .data(null)
        .build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "conversation_created",
        envelope
    );
  }

  // === CẬP NHẬT META NHÓM ===
  public void updateGroupMeta(
      String conversationId,
      GroupUpdateRequest req) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwnerOrAdmin(conv, me);

    // trước khi sửa, chụp old
    String oldName = conv.getName();
    String oldAvatar = conv.getAvatarUrl();
    String oldTopic = conv.getTopic();

    if (req.getName() != null) {
      conv.setName(req.getName());
    }
    if (req.getAvatarUrl() != null) {
      conv.setAvatarUrl(req.getAvatarUrl());
    }
    if (req.getTopic() != null) {
      conv.setTopic(req.getTopic());
    }
    conv.setModifiedDate(Instant.now());
    conv = conversationRepository.save(conv);

    var diff = ConversationMetaDiff.builder()
        .nameBefore(oldName).nameAfter(conv.getName())
        .avatarUrlBefore(oldAvatar).avatarUrlAfter(conv.getAvatarUrl())
        .topicBefore(oldTopic).topicAfter(conv.getTopic())
        .build();

    var envelope = eventHelper.baseEnvelope("conversation_updated", conv, me)
        .data(diff)
        .build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "conversation_updated",
        envelope
    );
  }


  // === THÊM THÀNH VIÊN ===
  public void addMembers(
      String conversationId,
      List<String> userIds) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwnerOrAdmin(conv, me);

    Set<String> exists =
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .collect(Collectors.toSet());
    List<String> toAdd =
        userIds.stream().filter(id -> !exists.contains(id)).toList();

    if (toAdd.isEmpty()) {
      return;
    }

    List<UserProfileSummary> sums =
        toAdd.stream().map(cache::getOrLoad).toList();
    conv.getParticipants().addAll(
        sums.stream()
            .map(chatMessageHelper::toParticipantInfoFromUserProfileSummary)
            .toList()
    );
    conv.setModifiedDate(Instant.now());
    conv = conversationRepository.save(conv);

    Instant now = Instant.now();
    for (String uid : toAdd) {
      memberRepository.save(ConversationMember.builder()
          .conversationId(conv.getId()).userId(uid).role("MEMBER").joinedAt(now)
          .build());
    }

    List<ParticipantInfo> addedInfos = conv.getParticipants().stream()
        .filter(p -> toAdd.contains(p.getUserId()))
        .toList();

    var data = MembersChangedData.builder().added(addedInfos).build();
    var envelope =
        eventHelper.baseEnvelope("members_added", conv, me).data(data).build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "members_added",
        envelope
    );
  }

  // === XOÁ THÀNH VIÊN ===
  public void removeMembers(
      String conversationId,
      List<String> userIds) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwnerOrAdmin(conv, me);

    // không xoá owner
    if (userIds.contains(conv.getOwnerId())) {
      Conversation finalConv = conv;
      userIds =
          userIds.stream().filter(u -> !u.equals(finalConv.getOwnerId()))
              .toList();
    }
    if (userIds.isEmpty()) {
      return;
    }

    List<String> removeUserIds = userIds;
    List<ParticipantInfo> removedInfos =
        conv.getParticipants().stream()
            .filter(p -> removeUserIds.contains(p.getUserId()))
            .toList();

    List<String> filterUserIds = userIds;
    conv.setParticipants(conv.getParticipants().stream()
        .filter(p -> !filterUserIds.contains(p.getUserId())).toList());
    List<String> finalUserIds = userIds;
    conv.setAdminIds(
        Optional.ofNullable(conv.getAdminIds()).orElseGet(List::of).stream()
            .filter(a -> !finalUserIds.contains(a)).toList());
    conv.setModifiedDate(Instant.now());
    conv = conversationRepository.save(conv);

    for (String uid : userIds) {
      memberRepository.deleteByConversationIdAndUserId(conv.getId(), uid);
    }

    var data = MembersChangedData.builder().removed(removedInfos).build();
    var envelope =
        eventHelper.baseEnvelope("members_removed", conv, me).data(data)
            .build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "members_removed",
        envelope
    );

    // gửi riêng cho người bị kick
    for (String uid : userIds) {
      var removed = removedInfos.stream().filter(p -> p.getUserId().equals(uid))
          .findFirst().orElse(null);
      var single =
          eventHelper.baseEnvelope("removed_from_conversation", conv, me)
              .data(Map.of("removed", removed))
              .build();
      push.pushToUsers(List.of(uid), "removed_from_conversation", single);
    }
  }

  // === THĂNG/HẠ QUYỀN ===
  public void setRole(
      String conversationId,
      String targetUserId, String role) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwner(conv, me); // chỉ owner

    boolean isParticipant = conv.getParticipants().stream()
        .anyMatch(p -> p.getUserId().equals(targetUserId));
    if (!isParticipant) {
      throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
    }

    String prevRole =
        memberRepository.findByConversationIdAndUserId(conversationId,
                targetUserId)
            .map(ConversationMember::getRole)
            .orElse("MEMBER");

    if ("OWNER".equals(role)) {
      conv.setOwnerId(targetUserId);

      List<String> newAdmins = Optional.ofNullable(conv.getAdminIds())
          .orElseGet(List::of)
          .stream()
          .filter(a -> !a.equals(targetUserId))
          .toList();

      // hạ người cũ xuống ADMIN hoặc MEMBER
      newAdmins = mergeAdmin(newAdmins, me);
      conv.setAdminIds(newAdmins);

      setMemberRole(conversationId, me, "ADMIN");
      setMemberRole(conversationId, targetUserId, "OWNER");
    } else if ("ADMIN".equals(role)) {
      conv.setAdminIds(mergeAdmin(conv.getAdminIds(), targetUserId));
      setMemberRole(conversationId, targetUserId, "ADMIN");
    } else { // MEMBER
      conv.setAdminIds(
          Optional.ofNullable(conv.getAdminIds()).orElseGet(List::of).stream()
              .filter(a -> !a.equals(targetUserId)).toList());
      setMemberRole(conversationId, targetUserId, "MEMBER");
    }

    conv.setModifiedDate(Instant.now());
    conv = conversationRepository.save(conv);

    var targetInfo = conversationHelper.findParticipant(conv, targetUserId);
    var data = RoleChangeData.builder()
        .target(targetInfo)
        .previousRole(prevRole)
        .newRole(role)
        .build();

    var envelope =
        eventHelper.baseEnvelope("roles_updated", conv, me).data(data).build();
    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "roles_updated",
        envelope
    );
  }

  // === RỜI NHÓM ===
  public void leaveGroup(
      String conversationId) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);

    // nếu owner rời nhóm: yêu cầu chuyển owner trước hoặc giải tán
    if (me.equals(conv.getOwnerId())) {
      throw new AppException(
          ErrorCode.UNCATEGORIZED_EXCEPTION); // hoặc custom code: OWNER_MUST_TRANSFER
    }

    conv.setParticipants(conv.getParticipants().stream()
        .filter(p -> !p.getUserId().equals(me)).toList());
    conv.setAdminIds(
        Optional.ofNullable(conv.getAdminIds()).orElseGet(List::of).stream()
            .filter(a -> !a.equals(me)).toList());
    conv.setModifiedDate(Instant.now());
    conversationRepository.save(conv);
    memberRepository.deleteByConversationIdAndUserId(conversationId, me);

    ParticipantInfo leaver =
        conversationHelper.findParticipant(conv, me); // null vì vừa xoá
    if (leaver == null) {
      // khôi phục nhanh từ cache, chỉ cần userId
      leaver = ParticipantInfo.builder().userId(me).build();
    }

    var data = MemberLeftData.builder().leaver(leaver).build();

    var envelope = eventHelper
        .baseEnvelope("member_left", conv, me)
        .data(data)
        .build();

    push.pushToUsers(
        conv.getParticipants().stream()
            .map(ParticipantInfo::getUserId)
            .toList(),
        "member_left",
        envelope
    );
  }

  // === GIẢI TÁN NHÓM ===
  public void deleteGroup(
      String conversationId) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwner(conv, me);

    conv.setDeleted(true);
    conv.setModifiedDate(Instant.now());
    conversationRepository.save(conv);

    // dọn member state để ngăn tính unread v.v.
    memberRepository.findAllByConversationId(conversationId)
        .forEach(m -> memberRepository.deleteByConversationIdAndUserId(
            conversationId, m.getUserId()));

    var envelope =
        eventHelper.baseEnvelope("conversation_deleted", conv, me).data(null)
            .build();
    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "conversation_deleted",
        envelope
    );
  }

  // === MUTE/UNMUTE ===
  public void setMute(
      String conversationId,
      Instant mutedUntil) {
    String me = AuthenticationHelper.getMyUserId();

    ConversationMember st =
        memberRepository.findByConversationIdAndUserId(conversationId, me)
            .orElseGet(() -> ConversationMember.builder()
                .conversationId(conversationId).userId(me)
                .joinedAt(Instant.now()).role("MEMBER").build());
    st.setMutedUntil(mutedUntil);
    memberRepository.save(st);
  }

  public void unmute(String conversationId) {
    setMute(conversationId, null);
  }

  public void setMemberMute(
      String conversationId,
      String targetUserId,
      Instant mutedUntil) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeGroupAndMember(conversationId, me);
    mustBeOwnerOrAdmin(conv, me);

    // đảm bảo target nằm trong nhóm
    boolean inGroup = conv.getParticipants().stream()
        .anyMatch(p -> p.getUserId().equals(targetUserId));
    if (!inGroup) {
      throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
    }

    ConversationMember st =
        memberRepository.findByConversationIdAndUserId(conversationId,
                targetUserId)
            .orElseGet(() -> ConversationMember.builder()
                .conversationId(conversationId).userId(targetUserId)
                .joinedAt(Instant.now()).role("MEMBER").build());
    st.setMutedUntil(mutedUntil);
    memberRepository.save(st);

    // thông báo cho người bị tác động & cả nhóm nếu muốn
    var targetInfo = conversationHelper.findParticipant(conv, targetUserId);
    var data =
        MuteData.builder().target(targetInfo).mutedUntil(mutedUntil).build();
    var envelope =
        eventHelper.baseEnvelope(
                mutedUntil == null ? "member_unmuted" : "member_muted",
                conv, me)
            .data(data).build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        mutedUntil == null ? "member_unmuted" : "member_muted",
        envelope
    );
  }

  public void unMemberMute(
      String conversationId,
      String targetUserId) {
    setMemberMute(conversationId, targetUserId, null);
  }

  // === ĐÁNH DẤU ĐÃ ĐỌC ===
  public void markRead(
      String conversationId,
      String upToMessageId,
      Instant upToTime) {
    String me = AuthenticationHelper.getMyUserId();
    Conversation conv = mustBeMember(conversationId, me);

    Instant boundaryTime = null;
    if (upToMessageId != null) {
      boundaryTime = chatMessageRepository.findById(upToMessageId)
          .map(ChatMessage::getCreatedDate)
          .orElse(null);
    }

    if (boundaryTime == null) {
      boundaryTime = (upToTime != null ? upToTime : Instant.now());
    }

    ConversationMember st =
        memberRepository.findByConversationIdAndUserId(conversationId, me)
            .orElseGet(() -> ConversationMember.builder()
                .conversationId(conversationId).userId(me)
                .joinedAt(Instant.now()).role("MEMBER").build());

    st.setLastReadAt(boundaryTime);
    st.setLastReadMessageId(upToMessageId);
    st.setUnreadCount(0L);
    memberRepository.save(st);

    var reader = conversationHelper.findParticipant(conv, me);
    var data = MessageReadData.builder()
        .reader(reader)
        .lastReadAt(boundaryTime)
        .lastReadMessageId(upToMessageId)
        .build();

    var envelope =
        eventHelper.baseEnvelope("message_read", conv, me).data(data).build();

    push.pushToUsers(
        conv.getParticipants().stream().map(ParticipantInfo::getUserId)
            .toList(),
        "message_read",
        envelope
    );
  }

  // === helper quyền/kiểm tra ===
  Conversation mustBeGroupAndMember(
      String conversationId,
      String userId) {
    Conversation conv = mustBeMember(conversationId, userId);
    if (!"GROUP".equals(conv.getType())) {
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
    return conv;
  }

  Conversation mustBeMember(
      String conversationId,
      String userId) {
    Conversation conv = conversationRepository.findById(conversationId)
        .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    if (Boolean.TRUE.equals(conv.getDeleted())) {
      throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
    }
    boolean in = conv.getParticipants().stream()
        .anyMatch(p -> p.getUserId().equals(userId));
    if (!in) {
      throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
    }
    return conv;
  }

  void mustBeOwner(
      Conversation conv,
      String userId) {
    if (!Objects.equals(conv.getOwnerId(), userId)) {
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
  }

  void mustBeOwnerOrAdmin(
      Conversation conv,
      String userId) {
    if (Objects.equals(conv.getOwnerId(), userId)) {
      return;
    }
    List<String> admins =
        Optional.ofNullable(conv.getAdminIds()).orElseGet(List::of);
    if (admins.contains(userId)) {
      return;
    }
    throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
  }

  List<String> mergeAdmin(
      List<String> adminIds,
      String target) {
    Set<String> set =
        new LinkedHashSet<>(Optional.ofNullable(adminIds).orElseGet(List::of));
    set.add(target);
    return new ArrayList<>(set);
  }

  void setMemberRole(
      String conversationId,
      String userId,
      String role) {
    memberRepository.findByConversationIdAndUserId(conversationId, userId)
        .ifPresent(m -> {
          m.setRole(role);
          memberRepository.save(m);
        });
  }
}
