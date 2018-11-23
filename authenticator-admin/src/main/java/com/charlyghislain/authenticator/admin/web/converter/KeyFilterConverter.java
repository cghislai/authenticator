package com.charlyghislain.authenticator.admin.web.converter;

import com.charlyghislain.authenticator.admin.api.domain.WsKeyFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class KeyFilterConverter {

    @Inject
    private ApplicationQueryService applicationQueryService;

    @NonNull
    public KeyFilter translateWsKeyFilter(@NonNull WsKeyFilter wsKeyFilter) {
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
        Optional.ofNullable(id).ifPresent(keyFilter::setId);
        Optional.ofNullable(active).ifPresent(keyFilter::setActive);
        Optional.ofNullable(forApplicationSecrets).ifPresent(keyFilter::setForApplicationSecrets);
        Optional.ofNullable(forApplication).ifPresent(keyFilter::setForApplication);
        applicationOptional.ifPresent(keyFilter::setApplication);
        Optional.ofNullable(name).ifPresent(keyFilter::setName);
        Optional.ofNullable(nameContains).ifPresent(keyFilter::setNameContains);
        return keyFilter;
    }

    @NonNull
    public Pagination<RsaKeyPair> translateWsPagination(@NonNull WsPagination input) {
        Pagination<RsaKeyPair> result = new Pagination<>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
