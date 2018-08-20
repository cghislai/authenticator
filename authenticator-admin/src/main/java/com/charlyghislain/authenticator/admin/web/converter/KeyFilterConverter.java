package com.charlyghislain.authenticator.admin.web.converter;

import com.charlyghislain.authenticator.admin.api.domain.WsKeyFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KeyFilterConverter {

    public KeyFilter translateWsKeyFilter(WsKeyFilter input) {
        if (input == null) {
            return null;
        }
        KeyFilter result = new KeyFilter();
        result.setId(input.getId());
        result.setActive(input.getActive());
        result.setForApplicationSecrets(input.getForApplicationSecrets());
        result.setForApplication(input.getForApplication());
//TODO
//result.setApplication(input.getApplication());
        result.setName(input.getName());
        result.setNameContains(input.getNameContains());
        return result;
    }

    public Pagination<RsaKeyPair> translateWsPagination(WsPagination input) {
        if (input == null) {
            return null;
        }
        Pagination<RsaKeyPair> result = new Pagination<>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
