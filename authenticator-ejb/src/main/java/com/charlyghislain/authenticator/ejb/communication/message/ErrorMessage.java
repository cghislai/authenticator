package com.charlyghislain.authenticator.ejb.communication.message;


import com.charlyghislain.authenticator.ejb.communication.template.AuthApplicationMessageTemplate;
import com.charlyghislain.authenticator.ejb.communication.template.ErrorMessageTemplate;
import com.charlyghislain.dispatcher.api.header.MailHeaders;
import com.charlyghislain.dispatcher.api.message.MessageDefinition;

@MessageDefinition(name = "error-message",
        templateContexts = {AuthApplicationMessageTemplate.class, ErrorMessageTemplate.class}
)
@MailHeaders(from = "${app.errorMailFrom}", to = "${app.errorMailTo}", subject = "Uncaught error: ${error.name}")
public class ErrorMessage {

}
