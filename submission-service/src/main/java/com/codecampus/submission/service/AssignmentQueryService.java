package com.codecampus.submission.service;

import com.codecampus.submission.dto.response.quiz.MyAssignmentResponse;
import com.codecampus.submission.helper.AssignmentHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentQueryService {

    AssignmentRepository assignmentRepository;

    AssignmentHelper assignmentHelper;

    @Transactional(readOnly = true)
    public List<MyAssignmentResponse> getAssignmentsForStudent(
            String studentId) {
        return assignmentRepository.findByStudentId(studentId)
                .stream()
                .map(a -> assignmentHelper.mapAssignmentToMyAssignmentResponse(
                        a, studentId))
                .toList();
    }

}
