package com.charlyghislain.authenticator.management.api.domain;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(
        name = "WsError",
        description = "An generic error",
        type = SchemaType.OBJECT
)
public class WsError implements Serializable {

    @NotNull
    private String code;
    @NotNull
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
