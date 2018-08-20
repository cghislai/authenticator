package com.charlyghislain.authenticator.domain.domain;


import com.charlyghislain.authenticator.domain.domain.util.WithId;
import com.charlyghislain.authenticator.domain.domain.util.WithName;
import com.charlyghislain.authenticator.domain.domain.validation.ValidEmail;
import com.charlyghislain.authenticator.domain.domain.validation.ValidIdentifierName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auth_user")
public class User implements WithId, WithName {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Size(max = 255)
    @NotNull
    @ValidIdentifierName
    private String name;

    @Column(name = "email")
    @Size(max = 255)
    @NotNull
    @ValidEmail
    private String email;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "active")
    @NotNull
    private boolean active;

    @Column(name = "admin")
    @NotNull
    private boolean admin;

    @Column(name = "password_expired")
    @NotNull
    private boolean passwordExpired;

    @Column(name = "email_verified")
    @NotNull
    private boolean emailVerified;

    @OneToMany(mappedBy = "user")
    @NotNull
    private List<UserApplication> userApplications = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String email) {
        this.name = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<UserApplication> getUserApplications() {
        return userApplications;
    }

    public void setUserApplications(List<UserApplication> userApplications) {
        this.userApplications = userApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
