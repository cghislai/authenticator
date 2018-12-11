package com.charlyghislain.authenticator.management.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Schema(
        name = "WsValidationError",
        description = "An validation error",
        type = SchemaType.OBJECT
)
public class WsValidationError implements Serializable {

    @NotNull
    private String code;
    @Nullable
    private String description;
    @Nullable
    private List<WsViolationError> violations;


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

    public List<WsViolationError> getViolations() {
        return violations;
    }

    public void setViolations(List<WsViolationError> violations) {
        this.violations = violations;
    }
}
