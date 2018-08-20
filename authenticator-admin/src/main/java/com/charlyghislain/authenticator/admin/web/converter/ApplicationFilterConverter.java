package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplicationFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationFilterConverter {

    public ApplicationFilter toApplicationFilter(WsApplicationFilter wsApplicationFilter) {
        Long id = wsApplicationFilter.getId();
        Boolean active = wsApplicationFilter.getActive();
        String name = wsApplicationFilter.getName();
        String nameContains = wsApplicationFilter.getNameContains();

        ApplicationFilter applicationFilter = new ApplicationFilter();
        applicationFilter.setId(id);
        applicationFilter.setActive(active);
        applicationFilter.setName(name);
        applicationFilter.setNameContains(nameContains);
        return applicationFilter;
    }

    public Pagination<Application> toPagination(WsPagination wsPagination) {
        return new Pagination<>(wsPagination.getOffset(), wsPagination.getLength());
    }
}
