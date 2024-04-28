package com.online.gallery.service.user;

import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.entity.user.User;

public interface UserService {
    String getUserId(User user);

    User getUser(User user);

    PresignedLinkResponse generatePresignedGetUrl(User user);

    PresignedLinkResponse generatePresignedPutUrl(
            ImageFileUploadRequest imageFileUploadRequest,
            User user);

    User saveProfileImage(Image profileImageToSave, User user);

    String updateUserPassword(String oldPassword,
                              String newPassword,
                              User user);
}