package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.management.api.domain.WsPagination;
import com.charlyghislain.authenticator.management.api.domain.WsUserApplicationFilter;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@ApplicationScoped
public class UserApplicationFilterConverter {

    @NonNull
    public UserApplicationFilter toUserApplicationFilter(@NonNull WsUserApplicationFilter wsUserApplicationFilter) {
        Long userId = wsUserApplicationFilter.getUserId();
        Long applicationId = wsUserApplicationFilter.getApplicationId();
        Boolean active = wsUserApplicationFilter.getActive();
        String applicationName = wsUserApplicationFilter.getApplicationName();
        String userName = wsUserApplicationFilter.getUserName();
        String userEmail = wsUserApplicationFilter.getUserEmail();
        String userNameContains = wsUserApplicationFilter.getUserNameContains();

        UserFilter userFilter = new UserFilter();
        userFilter.setActive(true);
        Optional.ofNullable(userId).ifPresent(userFilter::setId);
        Optional.ofNullable(userName).ifPresent(userFilter::setName);
        Optional.ofNullable(userEmail).ifPresent(userFilter::setEmail);
        Optional.ofNullable(userNameContains).ifPresent(userFilter::setNameContains);


        ApplicationFilter applicationFilter = new ApplicationFilter();
        applicationFilter.setActive(true);
        Optional.ofNullable(applicationId).ifPresent(applicationFilter::setId);
        Optional.ofNullable(applicationName).ifPresent(applicationFilter::setName);

        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        Optional.ofNullable(active).ifPresent(userApplicationFilter::setActive);
        userApplicationFilter.setUserFilter(userFilter);
        userApplicationFilter.setApplicationFilter(applicationFilter);
        return userApplicationFilter;
    }

    @NonNull
    @NotNull
    public Pagination<UserApplication> toPagination(@NonNull @NotNull WsPagination input) {
        Pagination<UserApplication> result = new Pagination<>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
