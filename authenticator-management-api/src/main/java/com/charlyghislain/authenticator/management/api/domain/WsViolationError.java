package com.charlyghislain.authenticator.management.api.domain;

import java.io.Serializable;

public class WsViolationError implements Serializable {

    private String fieldName;
    private String message;

    public WsViolationError(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
