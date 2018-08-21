package com.charlyghislain.authenticator.admin.web;


import com.charlyghislain.authenticator.admin.api.AdminKeyResource;
import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.admin.api.domain.WsKeyFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebError;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebException;
import com.charlyghislain.authenticator.admin.web.converter.KeyConverter;
import com.charlyghislain.authenticator.admin.web.converter.KeyFilterConverter;
import com.charlyghislain.authenticator.admin.web.converter.WsKeyConverter;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.CannotDeactivateSigningKey;
import com.charlyghislain.authenticator.domain.domain.exception.KeyIsLastActiveInScopeException;
import com.charlyghislain.authenticator.domain.domain.exception.KeyScopeChangedException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.service.RsaKeyPairConverterService;
import com.charlyghislain.authenticator.ejb.service.RsaKeyPairQueryService;
import com.charlyghislain.authenticator.ejb.service.RsaKeyPairUpdateService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

@RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
public class AdminKeyResourceController implements AdminKeyResource {

    @Inject
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;

    @Inject
    private WsKeyConverter wsKeyConverter;
    @Inject
    private RsaKeyPairUpdateService rsaKeyPairUpdateService;
    @Inject
    private KeyConverter keyConverter;
    @Inject
    private KeyFilterConverter keyFilterConverter;

    @Override
    public WsResultList<WsKey> listKeys(WsKeyFilter wsKeyFilter, WsPagination wsPagination) {
        KeyFilter keyFilter = keyFilterConverter.translateWsKeyFilter(wsKeyFilter);
        Pagination<RsaKeyPair> rsaKeyPairPagination = keyFilterConverter.translateWsPagination(wsPagination);

        ResultList<RsaKeyPair> rsaKeyPairs = rsaKeyPairQueryService.findRsaKeyPairs(keyFilter, rsaKeyPairPagination);
        List<WsKey> wsKeyList = rsaKeyPairs.map(wsKeyConverter::toWskey).getResults();
        return new WsResultList<>(wsKeyList, rsaKeyPairs.getTotalCount());
    }

    @Override
    public WsKey getKey(Long keyId) {
        return rsaKeyPairQueryService.findRsaKeyPairById(keyId)
                .map(wsKeyConverter::toWskey)
                .orElseThrow(this::newNotFoundException);
    }

    @Override
    public WsKey createKey(WsKey wsKey) {
        RsaKeyPair key = keyConverter.toRsaKeyPair(wsKey);
        try {
            RsaKeyPair rsaKeyPair = rsaKeyPairUpdateService.createNewKey(key);
            return wsKeyConverter.toWskey(rsaKeyPair);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public WsKey updateKey(Long keyId, WsKey wsKey) {
        RsaKeyPair keyUpdate = keyConverter.toRsaKeyPair(wsKey);
        RsaKeyPair key = rsaKeyPairQueryService.findRsaKeyPairById(keyId)
                .orElseThrow(this::newNotFoundException);
        try {
            RsaKeyPair updatedKey = rsaKeyPairUpdateService.updateKey(key, keyUpdate);
            return wsKeyConverter.toWskey(updatedKey);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        } catch (KeyIsLastActiveInScopeException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.LAST_ACTIVE_KEY_IN_SCOPE);
        } catch (KeyScopeChangedException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.KEY_SCOPE_CHANGED);
        } catch (CannotDeactivateSigningKey cannotDeactivateSigningKey) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.SIGNING_KEY_DEACTIVATION);
        }
    }

    @Override
    public String getPublicKeyPEM(Long keyId) {
        RsaKeyPair key = rsaKeyPairQueryService.findRsaKeyPairById(keyId)
                .orElseThrow(this::newNotFoundException);
        return rsaKeyPairConverterService.encodePublicKeyToPem(key);
    }


    private AuthenticatorAdminWebException newNotFoundException() {
        return new AuthenticatorAdminWebException(AuthenticatorAdminWebError.KEY_NOT_FOUND);
    }
}
