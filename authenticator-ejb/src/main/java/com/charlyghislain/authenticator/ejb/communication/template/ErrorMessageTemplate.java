package com.charlyghislain.authenticator.ejb.communication.template;

import com.charlyghislain.dispatcher.api.context.TemplateContext;
import com.charlyghislain.dispatcher.api.context.TemplateField;

@TemplateContext(key = "error")
public class ErrorMessageTemplate {

    @TemplateField(description = "message")
    private String message;
    @TemplateField(description = "stacktrace")
    private String stackTrace;
    @TemplateField(description = "name")
    private String name;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
