package com.charlyghislain.authenticator.domain.domain;


import com.charlyghislain.authenticator.domain.domain.util.WithId;
import com.charlyghislain.authenticator.domain.domain.util.WithName;
import com.charlyghislain.authenticator.domain.domain.validation.ValidIdentifierName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "rsa_key_pair")
public class RsaKeyPair implements WithId, WithName {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    @ValidIdentifierName
    private String name;

    @NotNull
    @Column(name = "modulus")
    private byte[] modulus;

    @NotNull
    @Column(name = "private_exponent")
    private byte[] privateExponent;

    @NotNull
    @Column(name = "public_exponent")
    private byte[] publicExponent;

    @NotNull
    @Column(name = "active")
    private boolean active;

    @NotNull
    @Column(name = "signing")
    private boolean signingKey;

    @NotNull
    @Column(name = "app_secret")
    private boolean forApplicationSecrets;

    @NotNull
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @JoinColumn(name = "application_id")
    @OneToOne
    private Application application;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String keyId) {
        this.name = keyId;
    }

    public byte[] getModulus() {
        return modulus;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public byte[] getPrivateExponent() {
        return privateExponent;
    }

    public void setPrivateExponent(byte[] privateExponent) {
        this.privateExponent = privateExponent;
    }

    public byte[] getPublicExponent() {
        return publicExponent;
    }

    public void setPublicExponent(byte[] publicExponent) {
        this.publicExponent = publicExponent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public Optional<Application> getApplication() {
        return Optional.ofNullable(application);
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isForApplicationSecrets() {
        return forApplicationSecrets;
    }

    public void setForApplicationSecrets(boolean useForApplicationSecrets) {
        this.forApplicationSecrets = useForApplicationSecrets;
    }

    public boolean isSigningKey() {
        return signingKey;
    }

    public void setSigningKey(boolean signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RsaKeyPair that = (RsaKeyPair) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
