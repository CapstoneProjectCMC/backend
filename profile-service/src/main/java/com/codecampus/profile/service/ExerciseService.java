package com.codecampus.profile.service;

import com.codecampus.profile.dto.request.ExerciseRequest;
import com.codecampus.profile.repository.ExerciseRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService
{
  ExerciseRepository exerciseRepository;
  UserProfileRepository userProfileRepository;
}
