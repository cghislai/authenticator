package com.charlyghislain.authenticator.admin.web.converter;

import com.charlyghislain.authenticator.admin.api.domain.WsKeyFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class KeyFilterConverter {

    @Inject
    private ApplicationQueryService applicationQueryService;

    public KeyFilter translateWsKeyFilter(WsKeyFilter wsKeyFilter) {
        Long id = wsKeyFilter.getId();
        Boolean active = wsKeyFilter.getActive();
        Boolean forApplicationSecrets = wsKeyFilter.getForApplicationSecrets();
        Boolean forApplication = wsKeyFilter.getForApplication();
        Long applicationId = wsKeyFilter.getApplicationId();
        String name = wsKeyFilter.getName();
        String nameContains = wsKeyFilter.getNameContains();

        Optional<Application> applicationOptional = Optional.ofNullable(applicationId)
                .flatMap(applicationQueryService::findApplicationById);

        KeyFilter keyFilter = new KeyFilter();
        keyFilter.setId(id);
        keyFilter.setActive(active);
        keyFilter.setForApplicationSecrets(forApplicationSecrets);
        keyFilter.setForApplication(forApplication);
        keyFilter.setApplication(applicationOptional.orElse(null));
        keyFilter.setName(name);
        keyFilter.setNameContains(nameContains);
        return keyFilter;
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
