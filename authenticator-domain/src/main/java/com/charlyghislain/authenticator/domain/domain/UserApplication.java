package com.charlyghislain.authenticator.domain.domain;


import com.charlyghislain.authenticator.domain.domain.util.WithId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "auth_user_application")
public class UserApplication implements WithId {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    @NotNull
    private User user;

    @JoinColumn(name = "application_id")
    @ManyToOne
    @NotNull
    private Application application;

    @Column(name = "active")
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserApplication that = (UserApplication) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
