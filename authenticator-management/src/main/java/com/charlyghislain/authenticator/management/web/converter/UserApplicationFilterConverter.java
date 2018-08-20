package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.management.api.domain.WsPagination;
import com.charlyghislain.authenticator.management.api.domain.WsUserApplicationFilter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserApplicationFilterConverter {

    public UserApplicationFilter toUserApplicationFilter(WsUserApplicationFilter wsUserApplicationFilter) {
        Long userId = wsUserApplicationFilter.getUserId();
        Long applicationId = wsUserApplicationFilter.getApplicationId();
        Boolean active = wsUserApplicationFilter.getActive();
        String applicationName = wsUserApplicationFilter.getApplicationName();
        String userName = wsUserApplicationFilter.getUserName();
        String userEmail = wsUserApplicationFilter.getUserEmail();
        String userNameContains = wsUserApplicationFilter.getUserNameContains();

        UserFilter userFilter = new UserFilter();
        userFilter.setId(userId);
        userFilter.setActive(true);
        userFilter.setName(userName);
        userFilter.setEmail(userEmail);
        userFilter.setNameContains(userNameContains);


        ApplicationFilter applicationFilter = new ApplicationFilter();
        applicationFilter.setId(applicationId);
        applicationFilter.setActive(true);
        applicationFilter.setName(applicationName);

        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.setActive(active);
        userApplicationFilter.setUserFilter(userFilter);
        userApplicationFilter.setApplicationFilter(applicationFilter);
        return userApplicationFilter;
    }

    public Pagination<UserApplication> toPagination(WsPagination input) {
        if (input == null) {
            return null;
        }
        Pagination<UserApplication> result = new Pagination<>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
