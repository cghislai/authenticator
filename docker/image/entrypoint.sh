#!/bin/bash

PRE_BOOT_FILE="/opt/payara/pre-boot-commands.sh"

DB_DRIVER="${DB_DRIVER:-mariadb}"
DB_USER="${DB_USER:-}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_PASSWORD_FILE="${DB_PASSWORD_FILE:-/var/run/secrets/db-password}"
DB_NAME="${DB_NAME:-}"
DB_SERVER="${DB_SERVER:-}"

if [[ -f "$DB_PASSWORD_FILE" && -z "$DB_PASSWORD" ]] ; then
    DB_PASSWORD="$(head -n1 ${DB_PASSWORD_FILE})"
fi

cat << EOF > ${PRE_BOOT_FILE}
set configs.config.server-config.admin-service.das-config.dynamic-reload-enabled=false

# Https only, even with an expired self-signed
set configs.config.server-config.network-config.network-listeners.network-listener.https-listener.enabled=true
set configs.config.server-config.network-config.network-listeners.network-listener.http-listener.enabled=false
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

exec "java" \
    ${JAVA_ARGS} \
    "-jar" "/opt/payara/payara-micro.jar" \
    "--prebootcommandfile" "${PRE_BOOT_FILE}" \
    "--addlibs" "/opt/payara/lib/" \
    "$@"
