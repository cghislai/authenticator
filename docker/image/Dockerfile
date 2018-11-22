FROM payara/micro:5.183

USER root
RUN apk update && apk add bash

USER payara
ADD entrypoint.sh /opt/payara/authenticator-entrypoint.sh
ADD https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar /opt/payara/lib/
ADD http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar  /opt/payara/lib/
ADD http://central.maven.org/maven2/org/slf4j/slf4j-jdk14/1.7.25/slf4j-jdk14-1.7.25.jar /opt/payara/lib/

ENTRYPOINT ["/opt/payara/authenticator-entrypoint.sh"]

CMD ["--deployFromGAV", "com.charlyghislain.authenticator,authenticator-web,1.0.1"]
