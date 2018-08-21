package com.charlyghislain.authenticator.domain.client;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.UserApplication;

public interface ApplicationUserEventsClient {

    void notifyUserAdded(String authToken, UserApplication userApplication);

    void notifyEmailVerified(String authToken, UserApplication userApplication);

    void notifyUserRemoved(String authToken, Application application, Long userId);

}
