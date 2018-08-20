package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;

public class UserApplicationFilterConverter {

    public Pagination<UserApplication> translateWsPagination(WsPagination input) {
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
