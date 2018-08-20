package com.charlyghislain.authenticator.domain.client;


import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;

import java.util.List;

public interface ApplicationAuthorizationClient {

    List<ApplicationRole> findUserApplicationRoles(String authToken, UserApplication userApplication);
}