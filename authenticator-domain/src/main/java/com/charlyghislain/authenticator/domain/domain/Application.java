package com.charlyghislain.authenticator.domain.domain;


import com.charlyghislain.authenticator.domain.domain.util.WithId;
import com.charlyghislain.authenticator.domain.domain.util.WithName;
import com.charlyghislain.authenticator.domain.domain.validation.ValidIdentifierName;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application implements WithId, WithName {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Size(max = 255)
    @ValidIdentifierName
    private String name;

    @Column(name = "url")
    @Size(max = 255)
    private String endpointUrl;

    @Column(name = "active")
    private boolean active;

    @NotNull
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name = "can_reset_user_password")
    private boolean canResetUserPassword = true;
    @Column(name = "can_verify_user_email")
    private boolean canVerifyUserEmail = true;
    @Column(name = "added_users_are_active")
    private boolean addedUsersAreActive = true;
    @Column(name = "existing_users_are_added_on_token_request")
    private boolean existingUsersAreAddedOnTokenRequest = true;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isCanResetUserPassword() {
        return canResetUserPassword;
    }

    public void setCanResetUserPassword(boolean canResetUserPassword) {
        this.canResetUserPassword = canResetUserPassword;
    }

    public boolean isCanVerifyUserEmail() {
        return canVerifyUserEmail;
    }

    public void setCanVerifyUserEmail(boolean canVerifyUserEmail) {
        this.canVerifyUserEmail = canVerifyUserEmail;
    }

    public boolean isAddedUsersAreActive() {
        return addedUsersAreActive;
    }

    public void setAddedUsersAreActive(boolean addedUsersAreActive) {
        this.addedUsersAreActive = addedUsersAreActive;
    }

    public boolean isExistingUsersAreAddedOnTokenRequest() {
        return existingUsersAreAddedOnTokenRequest;
    }

    public void setExistingUsersAreAddedOnTokenRequest(boolean existingUsersAreAddedOnTokenRequest) {
        this.existingUsersAreAddedOnTokenRequest = existingUsersAreAddedOnTokenRequest;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
