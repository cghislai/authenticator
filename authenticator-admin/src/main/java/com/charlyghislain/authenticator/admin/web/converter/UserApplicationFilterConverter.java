package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserApplicationFilterConverter {

    @NonNull
    public Pagination<UserApplication> toWsPagination(@NonNull WsPagination input) {
        Pagination<UserApplication> result = new Pagination<>();
        result.setOffset(input.getOffset());
        result.setLength(input.getLength());
//TODO
//result.setSorts(input.getSorts());
        return result;
    }
}
