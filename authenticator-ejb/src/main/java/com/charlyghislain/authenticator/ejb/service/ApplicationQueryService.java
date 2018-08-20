package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.client.ApplicationHealthClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.Application_;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.util.DbQueryUtils;
import com.charlyghislain.authenticator.ejb.util.FilterUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
public class ApplicationQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ApplicationHealthClient applicationHealthClient;
    @Inject
    private JwtTokenService tokenService;

    public Optional<Application> findActiveApplicationByName(String name) {
        ApplicationFilter ApplicationFilter = new ApplicationFilter();
        ApplicationFilter.setActive(true);
        ApplicationFilter.setName(name);
        return this.findApplication(ApplicationFilter);
    }

    public Optional<Application> findApplicationById(Long id) {
        ApplicationFilter ApplicationFilter = new ApplicationFilter();
        ApplicationFilter.setId(id);
        return this.findApplication(ApplicationFilter);
    }

    public Optional<Application> findActiveApplicationById(Long id) {
        ApplicationFilter ApplicationFilter = new ApplicationFilter();
        ApplicationFilter.setId(id);
        ApplicationFilter.setActive(true);
        return this.findApplication(ApplicationFilter);
    }

    private Optional<Application> findApplication(ApplicationFilter ApplicationFilter) {
        CriteriaQuery<Application> searchQuery = DbQueryUtils.createSearchQuery(entityManager, Application.class, ApplicationFilter, this::createPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    public ResultList<Application> findApplications(ApplicationFilter ApplicationFilter, Pagination<Application> Pagination) {
        CriteriaQuery<Application> searchQuery = DbQueryUtils.createSearchQuery(entityManager, Application.class, ApplicationFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, Pagination);
    }


    public ResultList<Application> findAllApplications(ApplicationFilter ApplicationFilter) {
        CriteriaQuery<Application> searchQuery = DbQueryUtils.createSearchQuery(entityManager, Application.class, ApplicationFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery);
    }

    public ApplicationAuthenticatorAuthorizationHealth checkProviderAuthorizationHealth(Application application) {
        String token = tokenService.generateAuthenticatorTokenForApplication(application);
        return applicationHealthClient.checkAuthenticatorAuthorizationHealth(application, token);
    }

    public ApplicationHealth checkApplicationHealth(Application application) {
        String token = tokenService.generateAuthenticatorTokenForApplication(application);
        return applicationHealthClient.checkApplicationHealth(application, token);
    }

    private List<Predicate> createPredicates(From<?, Application> applicationFrom, ApplicationFilter ApplicationFilter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Long> idPath = applicationFrom.get(Application_.id);
        Optional.ofNullable(ApplicationFilter.getId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<Boolean> activePath = applicationFrom.get(Application_.active);
        Optional.ofNullable(ApplicationFilter.getActive())
                .map(active -> criteriaBuilder.equal(activePath, active))
                .ifPresent(predicates::add);

        Path<String> namePath = applicationFrom.get(Application_.name);
        Optional.ofNullable(ApplicationFilter.getName())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Optional.ofNullable(ApplicationFilter.getNameContains())
                .map(FilterUtils::toSqlContainsString)
                .map(name -> criteriaBuilder.like(namePath, name))
                .ifPresent(predicates::add);

        return predicates;
    }

}
