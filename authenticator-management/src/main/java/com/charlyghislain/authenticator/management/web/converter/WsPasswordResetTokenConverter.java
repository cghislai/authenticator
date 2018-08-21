package com.charlyghislain.authenticator.management.web.converter;

import com.charlyghislain.authenticator.domain.domain.PasswordResetToken;
import com.charlyghislain.authenticator.management.api.domain.WsPasswordResetToken;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WsPasswordResetTokenConverter {

    public WsPasswordResetToken toWsPasswordResetToken(PasswordResetToken passwordResetToken) {
        String token = passwordResetToken.getToken();
        LocalDateTime creationTime = passwordResetToken.getCreationTime();
        LocalDateTime expirationTime = passwordResetToken.getExpirationTime();

        ZonedDateTime creationZonedDatetime = creationTime.atZone(ZoneId.systemDefault());
        ZonedDateTime expirationZonedDateTime = expirationTime.atZone(ZoneId.systemDefault());

        WsPasswordResetToken wsPasswordResetToken = new WsPasswordResetToken();
        wsPasswordResetToken.setCreationTime(creationZonedDatetime);
        wsPasswordResetToken.setExpirationTime(expirationZonedDateTime);
        wsPasswordResetToken.setToken(token);
        return wsPasswordResetToken;
    }
}
