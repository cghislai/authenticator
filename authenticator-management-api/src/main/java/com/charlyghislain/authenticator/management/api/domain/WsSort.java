package com.charlyghislain.authenticator.management.api.domain;

import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

public class WsSort implements Serializable {

    @Nullable
    private String field;
    private int order;

    public WsSort(String field, int order) {
        this.field = field;
        this.order = order;
    }

    @Nullable
    public String getField() {
        return field;
    }

    public int getOrder() {
        return order;
    }

    public static WsSort valueOf(String serializedSort) {
        String[] splitted = serializedSort.split("");
        if (splitted.length > 1) {
            String field = splitted[0];
            int order = splitted[1].equals("1") ? 1 : -1;
            return new WsSort(field, order);
        } else {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.SORTS_DESERIALIZATION_EXCEPTION,
                    "Invalid sorts value provided: " + serializedSort);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return field + ":" + (order == 1 ? "1" : "-1");
    }
}
