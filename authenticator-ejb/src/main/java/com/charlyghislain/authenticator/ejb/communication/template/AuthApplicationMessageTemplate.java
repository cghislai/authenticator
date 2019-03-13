package com.charlyghislain.authenticator.ejb.communication.template;

import com.charlyghislain.dispatcher.api.context.TemplateContext;
import com.charlyghislain.dispatcher.api.context.TemplateField;

@TemplateContext(key = "app", produced = true)
public class AuthApplicationMessageTemplate {

    @TemplateField(description = "name")
    private String name;
    @TemplateField(description = "url")
    private String url;
    @TemplateField(description = "error mail sender")
    private String errorMailFrom;
    @TemplateField(description = "error mail recipient")
    private String errorMailTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorMailFrom() {
        return errorMailFrom;
    }

    public void setErrorMailFrom(String errorMailFrom) {
        this.errorMailFrom = errorMailFrom;
    }

    public String getErrorMailTo() {
        return errorMailTo;
    }

    public void setErrorMailTo(String errorMailTo) {
        this.errorMailTo = errorMailTo;
    }
}
