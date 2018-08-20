package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsUserFilter;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserFilterConverter {

    public UserFilter translateWsUserFilter(WsUserFilter input) {
        if (input == null) {
            return null;
        }
        UserFilter result = new UserFilter();
        result.setId(input.getId());
        result.setActive(input.getActive());
        result.setName(input.getName());
        result.setEmail(input.getEmail());
        result.setNameContains(input.getNameContains());
        result.setPasswordExpired(input.getPasswordExpired());
        result.setAdmin(input.getAdmin());
        return result;
    }

    public Pagination<User> translateWsPagination(WsPagination input) {
        if (input == null) {
            return null;
        }
        Pagination<User> result = new Pagination<User>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
