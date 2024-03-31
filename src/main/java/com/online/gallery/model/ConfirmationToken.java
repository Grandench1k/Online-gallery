package com.online.gallery.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "confirmationToken")
public class ConfirmationToken {
    @Id
    private String id;
    private String userId;
    private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

    public ConfirmationToken(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }
}
