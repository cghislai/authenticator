package com.charlyghislain.authenticator.domain.domain.util;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

@FunctionalInterface
public interface SortExpressionMapper<T extends WithId> {

    Expression<? extends Comparable> apply(From<?, T> rootExpression);
}
