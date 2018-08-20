package com.charlyghislain.authenticator.admin.api.domain;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WsResultList<T extends Object> {

    private List<T> results;
    private Long totalCount;

    public WsResultList() {
        this.results = new ArrayList<>();
        this.totalCount = 0L;
    }

    public WsResultList(@NotNull List<T> results, @NotNull Long totalCount) {
        this.results = results;
        this.totalCount = totalCount;
    }

    public WsResultList(@NotNull List<T> results, @NotNull int totalCount) {
        this.results = results;
        this.totalCount = (long) totalCount;
    }

    @NotNull
    public List<T> getResults() {
        return results;
    }

    @NotNull
    public Long getTotalCount() {
        return totalCount;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
