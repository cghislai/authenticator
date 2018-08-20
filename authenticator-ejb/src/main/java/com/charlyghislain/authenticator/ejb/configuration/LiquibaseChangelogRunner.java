package com.charlyghislain.authenticator.ejb.configuration;


import com.charlyghislain.authenticator.ejb.util.LiquibaseChangelogRunnerUtils;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LiquibaseChangelogRunner {

    @Resource(lookup = "jdbc/authenticator")
    private DataSource dataSource;

    public void runChangeLogs() {
        LiquibaseChangelogRunnerUtils.runChangeLog(dataSource,
                "com/charlyghislain/authenticator/db/main.xml");
    }
}
