package com.online.gallery.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("profile_image_name")
    private String profileImageName;
    private Role role;
    private boolean enabled;

    public User(String id, String nickname, String email, String password,
                String profileImageName, Role role, boolean enabled) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageName = profileImageName;
        this.role = role;
        this.enabled = enabled;
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

    public String toString() {
        return "User(id=" + this.getId() + ", nickname=" + this.getNickname() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", profileImageName=" + this.getProfileImageName() + ", role=" + this.getRole() + ", enabled=" + this.isEnabled() + ")";
    }
}
