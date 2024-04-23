package com.online.gallery.model.user;

import com.online.gallery.exception.user.UserNotEnabledException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Document(collection = "users")
@Getter
@Setter
@Builder
public class User implements UserDetails {
    @Id
    private String id;
    private String nickname;
    private String email;
    private String password;
    private String profileImageId;
    private Role role;
    private boolean enabled;

    public User(String id, String nickname, String email, String password, String profileImageId, Role role, boolean enabled) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageId = profileImageId;
        this.role = role;
        this.enabled = enabled;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

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
            throw new UserNotEnabledException("please confirm registration with mail.");
        }
    }

    public String toString() {
        return "User(id=" + this.getId() + ", username=" + this.getNickname() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", profileImageId=" + this.getProfileImageId() + ", role=" + this.getRole() + ", enabled=" + this.isEnabled() + ")";
    }
}
