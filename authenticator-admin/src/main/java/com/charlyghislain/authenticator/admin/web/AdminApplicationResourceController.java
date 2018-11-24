package com.charlyghislain.authenticator.admin.web;

import com.charlyghislain.authenticator.admin.api.AdminApplicationResource;
import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationHealth;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebError;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebException;
import com.charlyghislain.authenticator.admin.web.converter.ApplicationConverter;
import com.charlyghislain.authenticator.admin.web.converter.ApplicationFilterConverter;
import com.charlyghislain.authenticator.admin.web.converter.WsApplicationConverter;
import com.charlyghislain.authenticator.admin.web.converter.WsApplicationHealthConverter;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.exception.ExistingActiveApplicationUsersException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.ApplicationUpdateService;
import com.charlyghislain.authenticator.ejb.service.JwtTokenService;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
public class AdminApplicationResourceController implements AdminApplicationResource {

    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private JwtTokenService tokenService;
    @Inject
    private ApplicationUpdateService applicationUpdateService;
    @Inject
    private WsApplicationHealthConverter wsApplicationHealthConverter;

    @Inject
    private WsApplicationConverter wsApplicationConverter;
    @Inject
    private ApplicationFilterConverter applicationFilterConverter;
    @Inject
    private ApplicationConverter applicationConverter;

    @NonNull
    @Override
    public WsResultList<WsApplication> listApplications(@NonNull WsApplicationFilter wsApplicationFilter, @NonNull WsPagination wsPagination) {
        ApplicationFilter applicationFilter = applicationFilterConverter.toApplicationFilter(wsApplicationFilter);
        Pagination<Application> applicationPagination = applicationFilterConverter.toPagination(wsPagination);

        ResultList<Application> applications = applicationQueryService.findApplications(applicationFilter, applicationPagination);
        List<WsApplication> wsResults = applications.map(wsApplicationConverter::toWsapplication)
                .getResults();
        return new WsResultList<>(wsResults, applications.getTotalCount());
    }

    @Override
    public WsApplication getApplication(Long applicationId) {
        return applicationQueryService.findApplicationById(applicationId)
                .map(wsApplicationConverter::toWsapplication)
                .orElseThrow(this::newNotFoundException);
    }

    @NonNull
    @Override
    public WsApplication updateApplication(Long applicationId, @NonNull WsApplication wsApplication) {
        Application application = applicationConverter.toWsApplication(wsApplication);
        Application existingApplication = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        try {
            Application updatedApplication = applicationUpdateService.updateApplication(existingApplication, application);
            return wsApplicationConverter.toWsapplication(updatedApplication);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public void deleteApplication(Long applicationId) {
        Application existingApplication = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        try {
            applicationUpdateService.removeApplication(existingApplication);
        } catch (ExistingActiveApplicationUsersException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.ACTIVE_USERS_EXIST);
        }
    }

    @NonNull
    @Override
    public WsApplication createApplication(@NonNull WsApplication wsApplication) {
        Application application = applicationConverter.toWsApplication(wsApplication);
        application.setCreationTime(LocalDateTime.now());
        try {
            Application updatedApplication = applicationUpdateService.createApplication(application);
            return wsApplicationConverter.toWsapplication(updatedApplication);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public String createApplicationToken(Long applicationId) {
        Application application = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        try {
            return tokenService.generateApplicationTokenForAuthenticator(application);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NO_SIGNING_KEY);
        }
    }

    @NonNull
    @Override
    public WsApplicationHealth getApplicationHealth(Long applicationId) {
        Application application = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        ApplicationAuthenticatorAuthorizationHealth providerAuthorizationHealth = applicationQueryService.checkProviderAuthorizationHealth(application);
        ApplicationHealth applicationHealth = applicationQueryService.checkApplicationHealth(application);
        return wsApplicationHealthConverter.toWsApplicationHealth(applicationHealth, providerAuthorizationHealth);
    }


    private AuthenticatorAdminWebException newNotFoundException() {
        return new AuthenticatorAdminWebException(AuthenticatorAdminWebError.APPLICAITON_NOT_FOUND);
    }
}
