package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.EmailVerificationToken;
import com.charlyghislain.authenticator.domain.domain.EmailVerificationToken_;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.UserApplication_;
import com.charlyghislain.authenticator.domain.domain.filter.EmailVerificationTokenFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.util.DbQueryUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class EmailVerificationQueryService {

    @PersistenceContext
    private EntityManager entityManager;


    @NonNull
    public Optional<EmailVerificationToken> findActiveEmailVerificationTokenForUserApplication(UserApplication userApplication) {
        EmailVerificationTokenFilter EmailVerificationTokenFilter = new EmailVerificationTokenFilter();
        EmailVerificationTokenFilter.setActive(true);
        EmailVerificationTokenFilter.setUserApplication(userApplication);
        return this.findEmailVerificationToken(EmailVerificationTokenFilter);
    }

    @NonNull
    public Optional<EmailVerificationToken> findActiveEmailVerificationTokenForUserApplication(UserApplication userApplication, String token) {
        EmailVerificationTokenFilter EmailVerificationTokenFilter = new EmailVerificationTokenFilter();
        EmailVerificationTokenFilter.setActive(true);
        EmailVerificationTokenFilter.setToken(token);
        EmailVerificationTokenFilter.setUserApplication(userApplication);
        return this.findEmailVerificationToken(EmailVerificationTokenFilter);
    }


    private Optional<EmailVerificationToken> findEmailVerificationToken(EmailVerificationTokenFilter EmailVerificationTokenFilter) {
        CriteriaQuery<EmailVerificationToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, EmailVerificationToken.class, EmailVerificationTokenFilter, this::createPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    @NonNull
    public ResultList<EmailVerificationToken> findEmailVerificationTokens(EmailVerificationTokenFilter EmailVerificationTokenFilter, @NonNull Pagination<EmailVerificationToken> Pagination) {
        CriteriaQuery<EmailVerificationToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, EmailVerificationToken.class, EmailVerificationTokenFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, Pagination);
    }

    @NonNull
    public ResultList<EmailVerificationToken> findAllEmailVerificationTokens(EmailVerificationTokenFilter EmailVerificationTokenFilter) {
        CriteriaQuery<EmailVerificationToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, EmailVerificationToken.class, EmailVerificationTokenFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery);
    }


    @NonNull
    private List<Predicate> createPredicates(From<?, EmailVerificationToken> emailVerificationTokenFrom, EmailVerificationTokenFilter emailVerificationTokenFilter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Long> idPath = emailVerificationTokenFrom.get(EmailVerificationToken_.id);
        Optional.ofNullable(emailVerificationTokenFilter.getId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<LocalDateTime> expirationPath = emailVerificationTokenFrom.get(EmailVerificationToken_.expirationTime);
        Optional.ofNullable(emailVerificationTokenFilter.getActive())
                .map(active -> this.createActivePredicate(active, expirationPath))
                .ifPresent(predicates::add);

        Path<String> namePath = emailVerificationTokenFrom.get(EmailVerificationToken_.token);
        Optional.ofNullable(emailVerificationTokenFilter.getToken())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Path<UserApplication> userApplicationPath = emailVerificationTokenFrom.get(EmailVerificationToken_.userApplication);
        Optional.ofNullable(emailVerificationTokenFilter.getUserApplication())
                .map(userApplication -> criteriaBuilder.equal(userApplicationPath, userApplication))
                .ifPresent(predicates::add);

        Join<EmailVerificationToken, UserApplication> userApplicationJoin = emailVerificationTokenFrom.join(EmailVerificationToken_.userApplication);
        Path<User> userPath = userApplicationJoin.get(UserApplication_.user);
        Optional.ofNullable(emailVerificationTokenFilter.getUser())
                .map(user -> criteriaBuilder.equal(userPath, user))
                .ifPresent(predicates::add);

        return predicates;
    }

    private Predicate createActivePredicate(Boolean active, Path<LocalDateTime> expirationPath) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Expression<LocalDateTime> nowLiteral = criteriaBuilder.literal(LocalDateTime.now());

        if (active) {
            return criteriaBuilder.greaterThan(expirationPath, nowLiteral);
        } else {
            return criteriaBuilder.lessThanOrEqualTo(expirationPath, nowLiteral);
        }
    }

}
