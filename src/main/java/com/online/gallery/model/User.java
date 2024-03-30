package com.online.gallery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.online.gallery.exception.UserNotEnabled;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String profileImageId;
    private Role role;
    private boolean enabled = false;
    private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        if (!this.enabled) {
            return this.getExpiredAt().isAfter(LocalDateTime.now());
        }
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void checkIfUserEnabled() {
        if (!this.isEnabled()) {
            throw new UserNotEnabled("please confirm registration with mail.");
        }
    }
}
