package com.charlyghislain.authenticator.ejb.communication.template;

import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.dispatcher.api.context.ProducedTemplateContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

public class MessageTemplateProducer {

    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_NAME)
    private String authenticatorName;

    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_URL)
    private String authenticatorUrl;

    @Inject
    @ConfigProperty(name = ConfigConstants.ERROR_MAIL_FROM)
    private Optional<String> errorMailSender;
    @Inject
    @ConfigProperty(name = ConfigConstants.ERROR_MAIL_TO)
    private Optional<String> errorMailRecipient;


    @Produces
    @Dependent
    @ProducedTemplateContext
    public AuthApplicationMessageTemplate getPlanCulApplicationTemplate() {
        AuthApplicationMessageTemplate applicationTemplate = new AuthApplicationMessageTemplate();
        applicationTemplate.setName(authenticatorName);
        applicationTemplate.setUrl(authenticatorUrl);
        applicationTemplate.setErrorMailFrom(errorMailSender.orElse(null));
        applicationTemplate.setErrorMailTo(errorMailRecipient.orElse(null));
        return applicationTemplate;
    }
}
