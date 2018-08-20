package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair_;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.util.DbQueryUtils;
import com.charlyghislain.authenticator.ejb.util.FilterUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class RsaKeyPairQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<RsaKeyPair> findRsaKeyPairById(Long id) {
        KeyFilter KeyFilter = new KeyFilter();
        KeyFilter.setId(id);
        return this.findRsaKeyPair(KeyFilter);
    }

    public Optional<RsaKeyPair> findActiveRsaKeyPairByName(String name) {
        KeyFilter KeyFilter = new KeyFilter();
        KeyFilter.setName(name);
        KeyFilter.setActive(true);

        return this.findRsaKeyPair(KeyFilter);
    }

    public Optional<RsaKeyPair> findRsaKeyPair(KeyFilter keyFilter) {
        CriteriaQuery<RsaKeyPair> searchQuery = DbQueryUtils.createSearchQuery(entityManager, RsaKeyPair.class,
                keyFilter, this::createPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    public ResultList<RsaKeyPair> findRsaKeyPairs(KeyFilter keyFilter, Pagination<RsaKeyPair> pagination) {
        CriteriaQuery<RsaKeyPair> searchQuery = DbQueryUtils.createSearchQuery(entityManager, RsaKeyPair.class,
                keyFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, pagination);
    }

    public ResultList<RsaKeyPair> findAllRsaKeyPairs(KeyFilter KeyFilter) {
        CriteriaQuery<RsaKeyPair> searchQuery = DbQueryUtils.createSearchQuery(entityManager, RsaKeyPair.class,
                KeyFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery);
    }

    private List<Predicate> createPredicates(From<?, RsaKeyPair> rsaKeyPairFrom, KeyFilter KeyFilter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Long> idPath = rsaKeyPairFrom.get(RsaKeyPair_.id);
        Optional.ofNullable(KeyFilter.getId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<Boolean> activePath = rsaKeyPairFrom.get(RsaKeyPair_.active);
        Optional.ofNullable(KeyFilter.getActive())
                .map(active -> criteriaBuilder.equal(activePath, active))
                .ifPresent(predicates::add);

        Path<Boolean> signingPath = rsaKeyPairFrom.get(RsaKeyPair_.signingKey);
        Optional.ofNullable(KeyFilter.getSigningKey())
                .map(signing -> criteriaBuilder.equal(signingPath, signing))
                .ifPresent(predicates::add);

        Path<Boolean> forApplicationSecretsPath = rsaKeyPairFrom.get(RsaKeyPair_.forApplicationSecrets);
        Optional.ofNullable(KeyFilter.getForApplicationSecrets())
                .map(forSecrets -> criteriaBuilder.equal(forApplicationSecretsPath, forSecrets))
                .ifPresent(predicates::add);

        Path<Application> applicationPath = rsaKeyPairFrom.get(RsaKeyPair_.application);
        Optional.ofNullable(KeyFilter.getForApplication())
                .filter(forComp -> !forComp)
                .map(notForComp -> criteriaBuilder.isNull(applicationPath))
                .ifPresent(predicates::add);

        Optional.ofNullable(KeyFilter.getForApplication())
                .filter(forComp -> forComp)
                .map(forComp -> criteriaBuilder.isNotNull(applicationPath))
                .ifPresent(predicates::add);

        Optional.ofNullable(KeyFilter.getApplication())
                .map(applicationId -> criteriaBuilder.equal(applicationPath, applicationId))
                .ifPresent(predicates::add);

        Path<String> namePath = rsaKeyPairFrom.get(RsaKeyPair_.name);
        Optional.ofNullable(KeyFilter.getName())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Optional.ofNullable(KeyFilter.getNameContains())
                .map(FilterUtils::toSqlContainsString)
                .map(name -> criteriaBuilder.like(namePath, name))
                .ifPresent(predicates::add);

        return predicates;
    }


}
