package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplicationFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ApplicationFilterConverter {

    @NonNull
    public ApplicationFilter toApplicationFilter(@NonNull WsApplicationFilter wsApplicationFilter) {
        Long id = wsApplicationFilter.getId();
        Boolean active = wsApplicationFilter.getActive();
        String name = wsApplicationFilter.getName();
        String nameContains = wsApplicationFilter.getNameContains();

        ApplicationFilter applicationFilter = new ApplicationFilter();
        Optional.ofNullable(id).ifPresent(applicationFilter::setId);
        Optional.ofNullable(active).ifPresent(applicationFilter::setActive);
        Optional.ofNullable(name).ifPresent(applicationFilter::setName);
        Optional.ofNullable(nameContains).ifPresent(applicationFilter::setNameContains);
        return applicationFilter;
    }

    @NonNull
    public Pagination<Application> toPagination(@NonNull WsPagination wsPagination) {
        return new Pagination<>(wsPagination.getOffset(), wsPagination.getLength());
    }
}
