package com.charlyghislain.authenticator.ejb.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseChangelogRunnerUtils {

    public static void runChangeLog(DataSource dataSource, String mainChangelogFile) {
        try (Connection connection = dataSource.getConnection()) {
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);

            ClassLoader classLoader = LiquibaseChangelogRunnerUtils.class.getClassLoader();
            if (classLoader == null) {
                throw new RuntimeException("Could not get a classloader");
            }
            ResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(classLoader);
            ResourceAccessor fileSystemResourceAccessor = new FileSystemResourceAccessor();
            CompositeResourceAccessor resourceAccessor = new CompositeResourceAccessor(classLoaderResourceAccessor, fileSystemResourceAccessor);

            Liquibase liquibase = new Liquibase(mainChangelogFile, resourceAccessor, database);
            liquibase.update("");
        } catch (SQLException e) {
            throw new RuntimeException("SQL error", e);
        } catch (DatabaseException e) {
            throw new RuntimeException("Database error", e);
        } catch (LiquibaseException e) {
            throw new RuntimeException("Liquibase error", e);
        }
    }
}
