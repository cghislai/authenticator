#!/bin/bash

PRE_BOOT_FILE="/opt/payara/pre-boot-commands.sh"
DB_DRIVER="${DB_DRIVER:-mariadb}"
DB_USER="${DB_USER:-}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_PASSWORD_FILE="${DB_PASSWORD_FILE:-/var/run/secrets/db-password}"
DB_NAME="${DB_NAME:-}"
DB_SERVER="${DB_SERVER:-}"

CONFIG_NAME="${CONFIG_NAME:-Authenticator}"
CONFIG_URL="${CONFIG_URL:-}"
CONFIG_TOKEN_ISSUER="${CONFIG_TOKEN_ISSUER:-}"
CONFIG_DEFAULT_ADMIN_MAIL="${CONFIG_DEFAULT_ADMIN_MAIL:-}"
CONFIG_CORS_ALLOWED_ORIGINS="${CONFIG_CORS_ALLOWED_ORIGINS:-*}"

if [[ -f "$DB_PASSWORD_FILE" && -z "$DB_PASSWORD" ]] ; then
    DB_PASSWORD="$(head -n1 ${DB_PASSWORD_FILE})"
fi
if [[ -z "$CONFIG_URL" ]] ; then
    echo "No CONFIG_URL provided" && exit 1
fi
if [[ -z "CONFIG_TOKEN_ISSUER" ]] ; then
    echo "No CONFIG_TOKEN_ISSUER provided" && exit 1
fi

cat << EOF >> ${PRE_BOOT_FILE}
set configs.config.server-config.admin-service.das-config.dynamic-reload-enabled=false

# Https only, even with an expired self-signed
set configs.config.server-config.network-config.network-listeners.network-listener.https-listener.enabled=true
set configs.config.server-config.network-config.network-listeners.network-listener.http-listener.enabled=false

set-config-property --source=domain --propertyValue="${CONFIG_NAME}" --propertyName="payara.microprofile.com.charlyghislain.authenticator.name"
set-config-property --source=domain --propertyValue="${CONFIG_URL}" --propertyName="payara.microprofile.com.charlyghislain.authenticator.url"
set-config-property --source=domain --propertyValue="${CONFIG_TOKEN_ISSUER}" --propertyName="payara.microprofile.com.charlyghislain.authenticator.token.issuer"
set-config-property --source=domain --propertyValue="${CONFIG_DEFAULT_ADMIN_MAIL}" --propertyName="payara.microprofile.com.charlyghislain.authenticator.admin.defaultEmail"
set-config-property --source=domain --propertyValue="${CONFIG_CORS_ALLOWED_ORIGINS}" --propertyName="payara.microprofile.com.charlyghislain.authenticator.cors.allowedOrigins"
EOF

if [[ "$DB_DRIVER" == "mariadb" ]] ; then
    if [[ -z "$DB_USER" || -z "$DB_PASSWORD" || -z "$DB_NAME" || -z "$DB_SERVER" ]] ; then
        echo "DB_USER, DB_NAME, DB_SERVER and DB_PASSWORD environement variables are required with the mariadb driver"
        echo "Use DB_DRIVER to use another driver"
        exit 1
    fi

    echo "Setting up the jdbc resource using the mariadb driver"
    cat << EOF >> ${PRE_BOOT_FILE}
create-jdbc-connection-pool --datasourceclassname org.mariadb.jdbc.MySQLDataSource --restype javax.sql.DataSource --property User="${DB_USER}":DatabaseName="${DB_NAME}":Password="${DB_PASSWORD}":ServerName="${DB_SERVER}" authPool
create-jdbc-resource --connectionpoolid=authPool jdbc/authenticator
EOF

elif [[ "$DB_DRIVER" == "h2" ]] ; then
  echo "Setting up the jdbc resource using the h2 driver"
  cat << EOF >> ${PRE_BOOT_FILE}
create-jdbc-connection-pool --datasourceclassname=org.h2.jdbcx.JdbcDataSource --restype=javax.sql.DataSource --property=URL=jdbc\:h2\:\${com.sun.aas.instanceRoot}/lib/databases/embedded_auth;AUTO_SERVER\=TRUE authPool
create-jdbc-resource --connectionpoolid=authPool jdbc/authenticator

EOF

else
    echo "Unsupported DB driver: ${DB_DRIVER}"
    exit 1
fi

exec "java" "-jar" "/opt/payara/payara-micro.jar" \
    "--prebootcommandfile" "${PRE_BOOT_FILE}" \
    "--addlibs" "/opt/payara/lib/" \
    "$@"