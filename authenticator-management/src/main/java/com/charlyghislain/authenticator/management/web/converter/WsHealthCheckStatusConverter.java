package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.management.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.management.api.domain.WsHealthStatus;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class WsHealthCheckStatusConverter {

    @NonNull
    public WsHealthCheckStatus toWsHealthCheckStatus(@NonNull HealthCheckResponse healthCheckResponse) {
        Map<String, Object> data = healthCheckResponse.getData()
                .orElseGet(HashMap::new);
        String name = healthCheckResponse.getName();
        HealthCheckResponse.State state = healthCheckResponse.getState();

        WsHealthCheckStatus checkStatus = new WsHealthCheckStatus();
        checkStatus.setData(data);
        checkStatus.setName(name);
        checkStatus.setState(state == HealthCheckResponse.State.UP ? WsHealthStatus.UP : WsHealthStatus.DOWN);
        return checkStatus;
    }
}
