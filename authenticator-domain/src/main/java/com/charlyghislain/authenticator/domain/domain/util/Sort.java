package com.charlyghislain.authenticator.domain.domain.util;

import javax.swing.SortOrder;

public class Sort<T extends WithId> {

    private SortOrder sortOrder;
    private SortExpressionMapper<T> sortExpressionMapper;

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortExpressionMapper<T> getSortExpressionMapper() {
        return sortExpressionMapper;
    }

    public void setSortExpressionMapper(SortExpressionMapper<T> sortExpressionMapper) {
        this.sortExpressionMapper = sortExpressionMapper;
    }
}
