FROM payara/micro:5.183

USER root
RUN apk update && apk add bash curl

ADD entrypoint.sh /opt/payara/authenticator-entrypoint.sh
ADD https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar /opt/payara/lib/
ADD http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar  /opt/payara/lib/
ADD http://central.maven.org/maven2/org/slf4j/slf4j-jdk14/1.7.25/slf4j-jdk14-1.7.25.jar /opt/payara/lib/
RUN chown -R payara /opt/payara

USER payara
ENTRYPOINT ["/opt/payara/authenticator-entrypoint.sh"]
HEALTHCHECK --start-period=120s CMD curl -k --fail https://localhost:8443/authenticator-web/info &>/dev/null

CMD ["--deployFromGAV", "com.charlyghislain.authenticator,authenticator-web,1.0.1"]
