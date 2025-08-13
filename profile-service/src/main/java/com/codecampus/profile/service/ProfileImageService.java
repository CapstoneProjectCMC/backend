package com.codecampus.profile.service;

import com.codecampus.profile.constant.file.FileType;
import com.codecampus.profile.dto.common.FileServiceResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.mapper.UserProfileMapper;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.repository.client.FileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileImageService {

    FileClient fileClient;
    UserProfileRepository userProfileRepository;
    UserProfileService userProfileService;
    UserProfileMapper userProfileMapper;

    public void uploadAvatar(MultipartFile file) {
        List<String> tags = List.of("avatar");

        String presigned = uploadToFileService(
                file, "avatar", tags);
        setAvatarUrl(presigned);
    }

    public void uploadBackground(MultipartFile file) {
        List<String> tags = List.of("background");

        String presigned = uploadToFileService(
                file, "background", tags);
        setBackgroundUrl(presigned);
    }

    public void updateAvatar(MultipartFile file) {
        List<String> tags = List.of("avatar");

        String presigned = uploadToFileService(
                file, "avatar", tags);
        setAvatarUrl(presigned);
    }

    public void updateBackground(MultipartFile file) {
        List<String> tags = List.of("background");

        String presigned = uploadToFileService(
                file, "background", tags);
        setBackgroundUrl(presigned);
    }

    String uploadToFileService(
            MultipartFile file,
            String description,
            List<String> tags) {
        FileServiceResponse<String> fileResponse =
                fileClient.uploadImage(
                        file,
                        FileType.Image,
                        description,
                        tags
                );
        return fileResponse.result();
    }

    void setAvatarUrl(String url) {
        UserProfile userProfile = userProfileService.getUserProfile();
        userProfile.setAvatarUrl(url);
        userProfileRepository.save(userProfile);
    }

    void setBackgroundUrl(String url) {
        UserProfile userProfile = userProfileService.getUserProfile();
        userProfile.setBackgroundUrl(url);
        userProfileRepository.save(userProfile);
    }
}
