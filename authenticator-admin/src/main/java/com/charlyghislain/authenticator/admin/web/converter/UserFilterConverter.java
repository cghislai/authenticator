package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsUserFilter;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserFilterConverter {

    @NonNull
    public UserFilter translateWsUserFilter(@NonNull WsUserFilter wsUserFilter) {
        Long id = wsUserFilter.getId();
        Boolean active = wsUserFilter.getActive();
        String name = wsUserFilter.getName();
        String email = wsUserFilter.getEmail();
        String nameContains = wsUserFilter.getNameContains();
        Boolean passwordExpired = wsUserFilter.getPasswordExpired();
        Boolean admin = wsUserFilter.getAdmin();

        UserFilter userFilter = new UserFilter();
        Optional.ofNullable(id).ifPresent(userFilter::setId);
        Optional.ofNullable(active).ifPresent(userFilter::setActive);
        Optional.ofNullable(name).ifPresent(userFilter::setName);
        Optional.ofNullable(email).ifPresent(userFilter::setEmail);
        Optional.ofNullable(nameContains).ifPresent(userFilter::setNameContains);
        Optional.ofNullable(passwordExpired).ifPresent(userFilter::setPasswordExpired);
        Optional.ofNullable(admin).ifPresent(userFilter::setAdmin);
        return userFilter;
    }

    @NonNull
    public Pagination<User> translateWsPagination(@NonNull WsPagination input) {
        Pagination<User> result = new Pagination<User>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
