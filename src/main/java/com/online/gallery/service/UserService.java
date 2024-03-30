package com.online.gallery.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import com.online.gallery.model.User;

import java.io.IOException;

public interface UserService {
    String createUserAndReturnUserId(Authentication authentication);

    User createUser(Authentication authentication);

    byte[] getProfileImage(User user);

    String saveProfileImage(MultipartFile profileImageFile, User user) throws IOException;

    String updateProfileImage(MultipartFile profileImageFile, User user) throws IOException;

    String updateUserPassword(String oldPassword, String newPassword, User user);
}
