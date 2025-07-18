package com.codecampus.submission.controller;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.dto.response.contest.ContestResponse;
import com.codecampus.submission.dto.response.contest.MyContestResponse;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.service.ContestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@RequestMapping("/contest")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContestController {

    ContestService contestService;

    // Giáo viên tạo contest
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ContestResponse> createContest(
            @RequestBody @Valid CreateContestRequest createContestRequest) {
        Contest contest = contestService.createContest(createContestRequest);

        int totalQuestions = contest.getExercises().stream()
                .mapToInt(e -> e.getExerciseType() == ExerciseType.QUIZ &&
                        e.getQuizDetail() != null
                        ? e.getQuizDetail().getNumQuestions()
                        : (e.getCodingDetail() != null ?
                        e.getCodingDetail().getTestCases().size() : 0))
                .sum();

        int totalDuration =
                contest.getExercises().stream().mapToInt(Exercise::getDuration)
                        .sum();

        return ApiResponse.<ContestResponse>builder()
                .result(new ContestResponse(
                        contest.getId(),
                        contest.getTitle(), contest.getDescription(),
                        contest.getStartTime(), contest.getEndTime(),
                        contest.isRankPublic(), contest.getRankRevealTime(),
                        totalQuestions, totalDuration,
                        contest.getAntiCheatConfig()
                ))
                .message("Tạo kỳ thi thành công!")
                .build();
    }

    // Học sinh xem contest của mình
    @GetMapping("/self")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<List<MyContestResponse>> myContests() {
        return ApiResponse.<List<MyContestResponse>>builder()
                .result(contestService.getMyContests())
                .message("Các kỳ thi của bạn!")
                .build();
    }
}
