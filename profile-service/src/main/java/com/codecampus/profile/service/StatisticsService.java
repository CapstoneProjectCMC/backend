package com.codecampus.profile.service;

import com.codecampus.profile.dto.response.UserPostStats;
import com.codecampus.profile.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {
    PostRepository postRepository;

    public UserPostStats getPostStats(String userId) {
        return UserPostStats.builder()
                .totalPosts(postRepository.countPostsOfUser(userId))
                .goodReactions(postRepository.countGoodReactions(userId))
                .build();
    }
}
