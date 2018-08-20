package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KeyConverter {

    public RsaKeyPair translateWsKey(WsKey input) {
        if (input == null) {
            return null;
        }
        RsaKeyPair result = new RsaKeyPair();
        result.setId(input.getId());
        result.setName(input.getName());
//TODO
//result.setModulus(input.getModulus());
//result.setPrivateExponent(input.getPrivateExponent());
//result.setPublicExponent(input.getPublicExponent());
        result.setActive(input.isActive());
        result.setForApplicationSecrets(input.isForApplicationSecrets());
//result.setCreationTime(input.getCreationTime());
//result.setApplication(input.getApplication());
        return result;
    }
}
