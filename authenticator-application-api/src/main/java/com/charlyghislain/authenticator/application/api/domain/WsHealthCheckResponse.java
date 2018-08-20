package com.charlyghislain.authenticator.application.api.domain;

import java.util.List;

public class WsHealthCheckResponse {

    private WsHealthStatus outcome;
    private List<WsHealthCheckStatus> checks;

    public WsHealthStatus getOutcome() {
        return outcome;
    }

    public void setOutcome(WsHealthStatus outcome) {
        this.outcome = outcome;
    }

    public List<WsHealthCheckStatus> getChecks() {
        return checks;
    }

    public void setChecks(List<WsHealthCheckStatus> checks) {
        this.checks = checks;
    }
}
