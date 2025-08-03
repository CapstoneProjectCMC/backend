package com.codecampus.profile.service;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.Exercise;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.repository.ExerciseRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

/**
 * Service xử lý các nghiệp vụ liên quan đến Exercise:
 * lưu, hủy lưu, và truy vấn danh sách các bài tập
 * đã lưu, đã hoàn thành hoặc do người dùng tạo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ExerciseRepository exerciseRepository;
    UserProfileRepository userProfileRepository;

    UserProfileService userProfileService;

    /**
     * Lưu một bài tập vào hồ sơ người dùng hiện tại.
     * <p>
     * Tìm profile của người dùng từ SecurityContext, sau đó tìm {@link Exercise}
     * tương ứng và thêm vào danh sách {@link SavedExercise}.
     *
     * @param exerciseId mã của bài tập cần lưu
     * @throws AppException với mã {@link ErrorCode#USER_NOT_FOUND}
     *                      nếu không tìm thấy người dùng,
     *                      hoặc {@link ErrorCode#EXERCISE_NOT_FOUND}
     *                      nếu không tìm thấy bài tập.
     */
    public void saveExercise(String exerciseId) {
        UserProfile profile = userProfileService.getUserProfile();

        Exercise exercise = getExercise(exerciseId);

        SavedExercise exerciseSaved = SavedExercise.builder()
                .saveAt(Instant.now())
                .exercise(exercise)
                .build();

        profile.getSavedExercises().add(exerciseSaved);
        userProfileRepository.save(profile);
    }

    /**
     * Hủy lưu (unsave) một bài tập đã lưu trước đó khỏi hồ sơ người dùng hiện tại.
     *
     * @param exerciseId mã của bài tập cần hủy lưu
     * @throws AppException với mã {@link ErrorCode#USER_NOT_FOUND}
     *                      nếu không tìm thấy người dùng.
     */
    public void unsaveExercise(String exerciseId) {
        UserProfile profile = userProfileService.getUserProfile();

        profile.getSavedExercises()
                .removeIf(exercise -> exercise.getId().equals(exerciseId));
        userProfileRepository.save(profile);
    }

    /**
     * Lấy trang dữ liệu các bài tập mà người dùng hiện tại đã lưu.
     *
     * @param page trang thứ mấy (bắt đầu từ 1)
     * @param size số phần tử trên mỗi trang
     * @return {@link PageResponse} chứa danh sách {@link SavedExercise}
     * đã lưu.
     */
    public PageResponse<SavedExercise> getSavedExercises(
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = userProfileRepository
                .findSavedExercises(AuthenticationHelper.getMyUserId(),
                        pageable);

        return toPageResponse(pageData, page);
    }

    /**
     * Lấy trang dữ liệu các bài tập mà người dùng hiện tại đã hoàn thành.
     *
     * @param page trang thứ mấy (bắt đầu từ 1)
     * @param size số phần tử trên mỗi trang
     * @return {@link PageResponse} chứa danh sách {@link CompletedExercise}
     * đã hoàn thành.
     */
    public PageResponse<CompletedExercise> getCompletedExercises(
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = userProfileRepository
                .findCompletedExercises(AuthenticationHelper.getMyUserId(),
                        pageable);

        return toPageResponse(pageData, page);
    }

    // ROLE TEACHER

    /**
     * (Dành cho ROLE_TEACHER) Lấy trang dữ liệu các bài tập do người dùng
     * hiện tại tạo ra.
     *
     * @param page trang thứ mấy (bắt đầu từ 1)
     * @param size số phần tử trên mỗi trang
     * @return {@link PageResponse} chứa danh sách {@link CreatedExercise}
     * do người dùng tạo.
     */
    public PageResponse<CreatedExercise> getCreatedExercises(
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = userProfileRepository
                .findCreatedExercises(AuthenticationHelper.getMyUserId(),
                        pageable);

        return toPageResponse(pageData, page);
    }

    public Exercise getExercise(String exerciseId) {
        return exerciseRepository
                .findByExerciseId(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }
}
