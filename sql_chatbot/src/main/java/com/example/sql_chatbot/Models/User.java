package com.example.sql_chatbot.Models;

import com.example.sql_chatbot.Models.enumerations.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "chatbot_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String username;
    private String password;
    private String huggingFaceAPIToken;

    @Enumerated
    private Role role;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;


    // Define relationships
    @OneToMany
    private Set<Database> databases;

    public User(String username, String password, String huggingFaceAPIToken, Role role) {
        this.username = username;
        this.password = password;
        this.huggingFaceAPIToken = huggingFaceAPIToken;
        this.role = role;
    }

    // Getters and setters

    public User() {

    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) role);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHuggingFaceAPIToken() {
        return huggingFaceAPIToken;
    }

    public void setHuggingFaceAPIToken(String huggingFaceAPIToken) {
        this.huggingFaceAPIToken = huggingFaceAPIToken;
    }

    public Set<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(Set<Database> databases) {
        this.databases = databases;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }
}


