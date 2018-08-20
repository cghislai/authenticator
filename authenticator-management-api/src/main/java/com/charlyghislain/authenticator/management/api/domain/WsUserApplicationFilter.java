package com.charlyghislain.authenticator.management.api.domain;

import com.charlyghislain.authenticator.management.api.NullableField;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.QueryParam;

public class WsUserApplicationFilter {

    @Nullable
    @NullableField
    @QueryParam("userId")
    private Long userId;
    @Nullable
    @NullableField
    @QueryParam("applicationId")
    private Long applicationId;
    @Nullable
    @NullableField
    @QueryParam("active")
    private Boolean active;


    @Nullable
    @NullableField
    @QueryParam("applicationName")
    private String applicationName;

    @Nullable
    @NullableField
    @QueryParam("userName")
    private String userName;
    @Nullable
    @NullableField
    @QueryParam("userEmail")
    private String userEmail;
    @Nullable
    @NullableField
    @QueryParam("userNameContains")
    private String userNameContains;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNameContains() {
        return userNameContains;
    }

    public void setUserNameContains(String userNameContains) {
        this.userNameContains = userNameContains;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
