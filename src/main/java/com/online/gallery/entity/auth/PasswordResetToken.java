package com.online.gallery.entity.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "passwordResetToken")
public class PasswordResetToken {
    @Id
    private String id;
    private String email;
    private LocalDateTime expiredAt;

    public PasswordResetToken(String id, String email) {
        this.id = id;
        this.email = email;
        this.expiredAt = LocalDateTime.now().plusMinutes(15);
    }

    public boolean isExpired() {
        return this.expiredAt.isBefore(LocalDateTime.now());
    }
}
