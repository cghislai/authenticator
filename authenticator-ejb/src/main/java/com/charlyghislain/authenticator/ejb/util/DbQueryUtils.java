package com.charlyghislain.authenticator.ejb.util;

import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.domain.domain.util.Sort;
import com.charlyghislain.authenticator.domain.domain.util.SortExpressionMapper;
import com.charlyghislain.authenticator.domain.domain.util.WithId;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.swing.SortOrder;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DbQueryUtils {


    public static <T extends WithId> Optional<T> toSingleResult(EntityManager entityManager,
                                                                CriteriaQuery<T> query) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        List<T> listResult = typedQuery.getResultList();
        return listResult.stream().findAny();
    }

    public static <T extends WithId> ResultList<T> toResultList(EntityManager entityManager,
                                                                CriteriaQuery<T> query) {
        Root<T> mainSelection = getMainSelection(query);
        Long countResult = getTotalCountResult(entityManager, query, mainSelection);

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        List<T> listResult = typedQuery.getResultList();

        return new ResultList<>(listResult, countResult);
    }

    public static <T extends WithId> ResultList<T> toResultList(EntityManager entityManager,
                                                                CriteriaQuery<T> query,
                                                                Pagination<T> pagination) {
        Root<T> mainSelection = getMainSelection(query);
        Long countResult = getTotalCountResult(entityManager, query, mainSelection);
        List<T> results = getOrderedListResult(entityManager, query, pagination, mainSelection);

        return new ResultList<>(results, countResult);
    }

    public static <T extends WithId, F> CriteriaQuery<T> createSearchQuery(EntityManager entityManager,
                                                                           Class<T> resultType, F filter,
                                                                           BiFunction<From<?, T>, F, List<Predicate>> filterMapper
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(resultType);
        Root<T> queryRoot = query.from(resultType);
        List<Predicate> predicates = filterMapper.apply(queryRoot, filter);

        query.select(queryRoot);
        query.where(predicates.toArray(new Predicate[0]));
        return query;
    }


    public static <T extends WithId> CriteriaQuery<T> createSearchQuery(EntityManager entityManager,
                                                                        Class<T> resultType,
                                                                        Function<From<?, T>, List<Predicate>> filterMapper
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(resultType);
        Root<T> queryRoot = query.from(resultType);
        List<Predicate> predicates = filterMapper.apply(queryRoot);

        query.select(queryRoot);
        query.where(predicates.toArray(new Predicate[0]));
        return query;
    }

    private static <T extends WithId> List<T> getOrderedListResult(EntityManager entityManager, CriteriaQuery<T> query, Pagination<T> Pagination, Root<T> mainSelection) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Order[] orders = Pagination.getSorts()
                .stream()
                .map(sort -> DbQueryUtils.mapSort(criteriaBuilder, mainSelection, sort))
                .toArray(Order[]::new);
        query.orderBy(orders);
        return getListResult(entityManager, query, Pagination);
    }

    private static <T extends WithId> Root<T> getMainSelection(CriteriaQuery<T> query) {
        return query.getRoots().stream()
                .filter(r -> r.getJavaType().isAssignableFrom(query.getResultType()))
                .map(r -> (Root<T>) r)
                .filter(r -> !r.isCorrelated())
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private static <T extends WithId> Long getTotalCountResult(EntityManager entityManager, CriteriaQuery<T> query, Root<T> mainSelection) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Expression<Long> count = criteriaBuilder.count(mainSelection);

        countQuery.select(count);
        countQuery.where(query.getRestriction());

        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        return countTypedQuery.getSingleResult();
    }

    private static <T extends WithId> List<T> getListResult(EntityManager entityManager, CriteriaQuery<T> query, Pagination Pagination) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(Pagination.getOffset());
        typedQuery.setMaxResults(Pagination.getLength());
        return typedQuery.getResultList();
    }

    private static <T extends WithId> Order mapSort(CriteriaBuilder criteriaBuilder,
                                                    Root<T> mainSelection,
                                                    Sort<T> sort) {

        SortExpressionMapper<T> sortExpressionMapper = sort.getSortExpressionMapper();
        Expression<? extends Comparable> sortExpression = sortExpressionMapper.apply(mainSelection);

        if (sort.getSortOrder() == SortOrder.ASCENDING) {
            return criteriaBuilder.asc(sortExpression);
        } else {
            return criteriaBuilder.desc(sortExpression);
        }
    }

}
