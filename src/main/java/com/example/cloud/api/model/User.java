package com.example.cloud.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String first_name;
    @Column(nullable = false)
    private String last_name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String username;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime account_created;
    @UpdateTimestamp
    private LocalDateTime account_updated;
    @Column
    private boolean verified;

    @Column
    private boolean isEmailSent;

    @Column
    private String token;

    @Column
    private LocalDateTime expirationTime;

    public User() {
    }

    public User(String first_name, String last_name, String password, String username) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.username = username;
        this.account_created = LocalDateTime.now();
        this.account_updated = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.account_created = LocalDateTime.now();
        this.account_updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.account_updated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return false;
    }

    public LocalDateTime getAccount_created() {
        return account_created;
    }

    public LocalDateTime getAccount_updated() {
        return account_updated;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }



    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", account_created=" + account_created +
                ", account_updated=" + account_updated +
                '}';
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setEmailSent(boolean emailSent) {
        isEmailSent = emailSent;
    }

    @JsonIgnore
    public boolean isVerified() {
        return verified;
    }

    @JsonIgnore
    public boolean isEmailSent() {
        return isEmailSent;
    }


    @JsonIgnore
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @JsonIgnore
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}

