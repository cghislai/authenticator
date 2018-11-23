package com.charlyghislain.authenticator.management.web.converter;

import com.charlyghislain.authenticator.domain.domain.EmailVerificationToken;
import com.charlyghislain.authenticator.management.api.domain.WsEmailVerificationToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WsEmailVerificationTokenConverter {

    @NonNull
    public WsEmailVerificationToken toWsEmailVerificationToken(@NonNull EmailVerificationToken emailVerificationToken) {
        String token = emailVerificationToken.getToken();
        LocalDateTime creationTime = emailVerificationToken.getCreationTime();
        LocalDateTime expirationTime = emailVerificationToken.getExpirationTime();

        ZonedDateTime creationZonedDatetime = creationTime.atZone(ZoneId.systemDefault());
        ZonedDateTime expirationZonedDateTime = expirationTime.atZone(ZoneId.systemDefault());

        WsEmailVerificationToken wsEmailVerificationToken = new WsEmailVerificationToken();
        wsEmailVerificationToken.setCreationTime(creationZonedDatetime);
        wsEmailVerificationToken.setExpirationTime(expirationZonedDateTime);
        wsEmailVerificationToken.setToken(token);
        return wsEmailVerificationToken;
    }
}
