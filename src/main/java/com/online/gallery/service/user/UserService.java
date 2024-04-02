package com.online.gallery.service.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import com.online.gallery.model.user.User;

import java.io.IOException;

public interface UserService {
    String getUserId(Authentication authentication);

    User getUser(Authentication authentication);

    String getFileFormat(MultipartFile videoFile);

    byte[] getProfileImage(User user);

    String saveProfileImage(MultipartFile profileImageFile, User user) throws IOException;

    String updateProfileImage(MultipartFile profileImageFile, User user) throws IOException;

    String updateUserPassword(String oldPassword, String newPassword, User user);
}
