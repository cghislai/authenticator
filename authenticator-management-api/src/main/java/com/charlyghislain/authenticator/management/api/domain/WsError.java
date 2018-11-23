package com.charlyghislain.authenticator.management.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;
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
    @Nullable
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
