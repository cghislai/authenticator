package com.charlyghislain.authenticator.domain.domain.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

@FunctionalInterface
public interface SortExpressionMapper<T extends WithId> {

    @NonNull Expression<? extends Comparable> apply(From<?, T> rootExpression);
}
