package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.Application_;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.UserApplication_;
import com.charlyghislain.authenticator.domain.domain.User_;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserQueryService {

    @Inject
    private PasswordHash passwordHash;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findUserByName(String name) {
        UserFilter UserFilter = new UserFilter();
        UserFilter.setName(name);
        return this.findUser(UserFilter);
    }

    public Optional<User> findUserById(Long id) {
        UserFilter UserFilter = new UserFilter();
        UserFilter.setId(id);
        return this.findUser(UserFilter);
    }

    public Optional<User> findUser(UserFilter UserFilter) {
        CriteriaQuery<User> searchQuery = DbQueryUtils.createSearchQuery(entityManager, User.class, UserFilter, this::createPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    public ResultList<User> findAllUsers(UserFilter UserFilter) {
        CriteriaQuery<User> searchQuery = DbQueryUtils.createSearchQuery(entityManager, User.class, UserFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery);
    }

    public ResultList<User> findUsers(UserFilter UserFilter, Pagination<User> Pagination) {
        CriteriaQuery<User> searchQuery = DbQueryUtils.createSearchQuery(entityManager, User.class, UserFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, Pagination);
    }

    public Optional<UserApplication> findActiveUserApplication(User user, Application application) {
        UserApplicationFilter userApplicationFilter = createUserApplicationFilter(user, application);
        userApplicationFilter.setActive(true);
        return this.findUserApplication(userApplicationFilter);
    }

    public Optional<UserApplication> findUserApplication(User user, Application application) {
        UserApplicationFilter userApplicationFilter = createUserApplicationFilter(user, application);
        return this.findUserApplication(userApplicationFilter);
    }

    public Optional<UserApplication> findUserApplication(UserApplicationFilter userApplicationFilter) {
        CriteriaQuery<UserApplication> searchQuery = DbQueryUtils.createSearchQuery(entityManager, UserApplication.class, userApplicationFilter,
                this::createUserApplicationPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    public ResultList<UserApplication> findUserApplications(UserApplicationFilter userApplicationFilter, Pagination<UserApplication> Pagination) {
        CriteriaQuery<UserApplication> searchQuery = DbQueryUtils.createSearchQuery(entityManager, UserApplication.class, userApplicationFilter,
                this::createUserApplicationPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, Pagination);
    }

    private List<Predicate> createUserApplicationPredicates(From<?, UserApplication> root, UserApplicationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        UserFilter userFilter = filter.getUserFilter();
        ApplicationFilter applicationFilter = filter.getApplicationFilter();

        Join<UserApplication, User> userJoin = root.join(UserApplication_.user);
        Path<Long> userIdPath = userJoin.get(User_.id);
        Optional.ofNullable(userFilter.getId())
                .map(id -> criteriaBuilder.equal(userIdPath, id))
                .ifPresent(predicates::add);

        Join<UserApplication, Application> applicationJoin = root.join(UserApplication_.application);
        Path<Long> applicationIdPath = applicationJoin.get(Application_.id);
        Optional.ofNullable(applicationFilter.getId())
                .map(id -> criteriaBuilder.equal(applicationIdPath, id))
                .ifPresent(predicates::add);

        Path<Boolean> activePath = root.get(UserApplication_.active);
        Optional.ofNullable(filter.getActive())
                .map(active -> criteriaBuilder.equal(activePath, active))
                .ifPresent(predicates::add);

        Path<String> namePath = userJoin.get(User_.name);
        Optional.ofNullable(userFilter.getName())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Path<String> emailPath = userJoin.get(User_.email);
        Optional.ofNullable(userFilter.getEmail())
                .map(email -> criteriaBuilder.equal(emailPath, email))
                .ifPresent(predicates::add);

        Optional.ofNullable(userFilter.getNameContains())
                .map(FilterUtils::toSqlContainsString)
                .map(name -> criteriaBuilder.like(namePath, name))
                .ifPresent(predicates::add);

        return predicates;
    }

    private List<Predicate> createPredicates(From<?, User> userFrom, UserFilter UserFilter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Long> idPath = userFrom.get(User_.id);
        Optional.ofNullable(UserFilter.getId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<Boolean> activePath = userFrom.get(User_.active);
        Optional.ofNullable(UserFilter.getActive())
                .map(active -> criteriaBuilder.equal(activePath, active))
                .ifPresent(predicates::add);

        Path<String> namePath = userFrom.get(User_.name);
        Optional.ofNullable(UserFilter.getName())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Path<String> emailPath = userFrom.get(User_.email);
        Optional.ofNullable(UserFilter.getEmail())
                .map(email -> criteriaBuilder.equal(emailPath, email))
                .ifPresent(predicates::add);

        Optional.ofNullable(UserFilter.getNameContains())
                .map(FilterUtils::toSqlContainsString)
                .map(name -> criteriaBuilder.like(namePath, name))
                .ifPresent(predicates::add);

        Path<Boolean> passwordExpiredPath = userFrom.get(User_.passwordExpired);
        Optional.ofNullable(UserFilter.getPasswordExpired())
                .map(name -> criteriaBuilder.equal(passwordExpiredPath, name))
                .ifPresent(predicates::add);

        Path<Boolean> adminPath = userFrom.get(User_.admin);
        Optional.ofNullable(UserFilter.getAdmin())
                .map(name -> criteriaBuilder.equal(adminPath, name))
                .ifPresent(predicates::add);

        return predicates;
    }

    @NotNull
    private UserApplicationFilter createUserApplicationFilter(User user, Application application) {
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        UserFilter userFilter = new UserFilter();
        userFilter.setId(user.getId());
        userApplicationFilter.setUserFilter(userFilter);
        ApplicationFilter applicationFilter = new ApplicationFilter();
        applicationFilter.setId(application.getId());
        userApplicationFilter.setApplicationFilter(applicationFilter);
        return userApplicationFilter;
    }

}
