package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorException;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.ValidationException;
import com.charlyghislain.authenticator.ejb.communication.message.ErrorMessage;
import com.charlyghislain.authenticator.ejb.communication.template.ErrorMessageTemplate;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.dispatcher.api.context.TemplateContextObject;
import com.charlyghislain.dispatcher.api.dispatching.DispatchedMessage;
import com.charlyghislain.dispatcher.api.exception.MessageRenderingException;
import com.charlyghislain.dispatcher.api.message.DispatcherMessage;
import com.charlyghislain.dispatcher.api.message.Message;
import com.charlyghislain.dispatcher.api.rendering.ReadyToBeRenderedMessage;
import com.charlyghislain.dispatcher.api.rendering.ReadyToBeRenderedMessageBuilder;
import com.charlyghislain.dispatcher.api.rendering.RenderedMessage;
import com.charlyghislain.dispatcher.api.service.MessageDispatcher;
import com.charlyghislain.dispatcher.api.service.MessageRenderer;
import com.charlyghislain.dispatcher.api.service.TemplateContextsService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class ErrorService {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorService.class);

    @Inject
    @ConfigProperty(name = ConfigConstants.ERRORS_SEND_MAIL)
    private Boolean sendErrorMails;

    @Inject
    @Message(ErrorMessage.class)
    private DispatcherMessage errorMessage;

    @Inject
    private TemplateContextsService templateContextsService;
    @Inject
    private MessageRenderer messageRenderer;
    @Inject
    private MessageDispatcher messageDispatcher;

    public AuthenticatorRuntimeException handleError(Throwable e) {
        AuthenticatorRuntimeException authenticatorException = this.wrapException(e);
        if (sendErrorMails) {
            this.sendErrorMail(authenticatorException);
        }
        return authenticatorException;
    }

    private void sendErrorMail(Throwable e) {
        try {
            StringWriter stackTraceWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTraceWriter));
            stackTraceWriter.close();
            String stackTrace = stackTraceWriter.toString();

            String exceptionName = e.getClass().getName();

            ErrorMessageTemplate errorMessageTemplate = new ErrorMessageTemplate();
            errorMessageTemplate.setMessage(e.getMessage());
            errorMessageTemplate.setStackTrace(stackTrace);
            errorMessageTemplate.setName(exceptionName);

            List<TemplateContextObject> templateContexts = templateContextsService.createTemplateContexts(
                    errorMessage, errorMessageTemplate);

            ReadyToBeRenderedMessage readyToBeRenderedMessage = ReadyToBeRenderedMessageBuilder.newBuider(errorMessage)
                    .withContext(templateContexts)
                    .acceptMailDispatching()
                    .acceptLocale(Locale.ENGLISH)
                    .build();

            dispatchMessage(readyToBeRenderedMessage);
        } catch (AuthenticatorException e1) {
            LOG.error("Could not send error mail", e1);
            LOG.error(" original error was ", e);
        } catch (IOException e1) {


        }
    }

    @NotNull
    private DispatchedMessage dispatchMessage(ReadyToBeRenderedMessage readyToBeRenderedMessage) throws AuthenticatorException {
        RenderedMessage renderedMessage;
        try {
            renderedMessage = messageRenderer.renderMessage(readyToBeRenderedMessage);
        } catch (MessageRenderingException e) {
            throw new AuthenticatorException("Could not render message", e);
        }

        DispatchedMessage dispatchedMessage = messageDispatcher.dispatchMessage(renderedMessage);
        boolean anySucceeded = dispatchedMessage.isAnySucceeded();
        if (!anySucceeded) {
            throw new AuthenticatorException("Could not dispatch message", dispatchedMessage.getMultiErrorsException());
        }
        return dispatchedMessage;
    }


    private AuthenticatorRuntimeException wrapException(Throwable e) {
        if (e instanceof ConstraintViolationException) {
            // Hide parameter values
            ConstraintViolationException violationException = (ConstraintViolationException) e;
            AuthenticatorException validationError = new ValidationException(violationException.getConstraintViolations());
            return new AuthenticatorRuntimeException(validationError);
        }
        return new AuthenticatorRuntimeException(e);
    }

}
