package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.PasswordResetToken;
import com.charlyghislain.authenticator.domain.domain.PasswordResetToken_;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.filter.PasswordResetTokenFilter;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.util.DbQueryUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class PasswordResetTokenQueryService {

    @PersistenceContext
    private EntityManager entityManager;


    public Optional<PasswordResetToken> findActivePasswordResetTokenForUser(User user) {
        PasswordResetTokenFilter PasswordResetTokenFilter = new PasswordResetTokenFilter();
        PasswordResetTokenFilter.setActive(true);
        PasswordResetTokenFilter.setUser(user);
        return this.findPasswordResetToken(PasswordResetTokenFilter);
    }

    public Optional<PasswordResetToken> findActivePasswordResetTokenForUser(User user, String token) {
        PasswordResetTokenFilter PasswordResetTokenFilter = new PasswordResetTokenFilter();
        PasswordResetTokenFilter.setActive(true);
        PasswordResetTokenFilter.setToken(token);
        PasswordResetTokenFilter.setUser(user);
        return this.findPasswordResetToken(PasswordResetTokenFilter);
    }


    private Optional<PasswordResetToken> findPasswordResetToken(PasswordResetTokenFilter PasswordResetTokenFilter) {
        CriteriaQuery<PasswordResetToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, PasswordResetToken.class, PasswordResetTokenFilter, this::createPredicates);
        return DbQueryUtils.toSingleResult(entityManager, searchQuery);
    }

    public ResultList<PasswordResetToken> findPasswordResetTokens(PasswordResetTokenFilter PasswordResetTokenFilter, Pagination<PasswordResetToken> Pagination) {
        CriteriaQuery<PasswordResetToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, PasswordResetToken.class, PasswordResetTokenFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery, Pagination);
    }

    public ResultList<PasswordResetToken> findAllPasswordResetTokens(PasswordResetTokenFilter PasswordResetTokenFilter) {
        CriteriaQuery<PasswordResetToken> searchQuery = DbQueryUtils.createSearchQuery(entityManager, PasswordResetToken.class, PasswordResetTokenFilter, this::createPredicates);
        return DbQueryUtils.toResultList(entityManager, searchQuery);
    }


    private List<Predicate> createPredicates(From<?, PasswordResetToken> passwordResetTokenFrom, PasswordResetTokenFilter passwordResetTokenFilter) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Long> idPath = passwordResetTokenFrom.get(PasswordResetToken_.id);
        Optional.ofNullable(passwordResetTokenFilter.getId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<LocalDateTime> expirationPath = passwordResetTokenFrom.get(PasswordResetToken_.expirationTime);
        Optional.ofNullable(passwordResetTokenFilter.getActive())
                .map(active -> this.createActivePredicate(active, expirationPath))
                .ifPresent(predicates::add);

        Path<String> namePath = passwordResetTokenFrom.get(PasswordResetToken_.token);
        Optional.ofNullable(passwordResetTokenFilter.getToken())
                .map(name -> criteriaBuilder.equal(namePath, name))
                .ifPresent(predicates::add);

        Path<User> userPath = passwordResetTokenFrom.get(PasswordResetToken_.user);
        Optional.ofNullable(passwordResetTokenFilter.getUser())
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
