/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos;

import com.floreantpos.Messages;
import org.apache.commons.lang.StringUtils;

public enum Database {
    DERBY_SINGLE(Messages.getString("Database.DERBY_SINGLE"), "jdbc:derby:database/derby-single/posdb", "jdbc:derby:database/derby-single/posdb;create=true", "", "org.apache.derby.jdbc.EmbeddedDriver", "org.hibernate.dialect.DerbyDialect"),
    DERBY_SERVER(Messages.getString("Database.DERBY_SERVER"), "jdbc:derby://<host>:<port>/<db>", "jdbc:derby://<host>:<port>/<db>;create=true", "51527", "org.apache.derby.jdbc.ClientDriver", "org.hibernate.dialect.DerbyDialect"),
    MYSQL(Messages.getString("Database.MYSQL"), "jdbc:mysql://<host>:<port>/<db>?characterEncoding=UTF-8", "jdbc:mysql://<host>:<port>/<db>?characterEncoding=UTF-8", "3306", "com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQLDialect"),
    POSTGRES(Messages.getString("Database.POSTGRES"), "jdbc:postgresql://<host>:<port>/<db>", "jdbc:postgresql://<host>:<port>/<db>", "5432", "org.postgresql.Driver", "org.hibernate.dialect.PostgreSQLDialect");

    private String providerName;
    private String jdbcUrlFormat;
    private String jdbcUrlFormatToCreateDb;
    private String defaultPort;
    private String driverClass;
    private String hibernateDialect;

    private Database(String providerName, String jdbcURL, String jdbcURL2CreateDb, String defaultPort, String driverClass, String hibernateDialect) {
        this.providerName = providerName;
        this.jdbcUrlFormat = jdbcURL;
        this.jdbcUrlFormatToCreateDb = jdbcURL2CreateDb;
        this.defaultPort = defaultPort;
        this.driverClass = driverClass;
        this.hibernateDialect = hibernateDialect;
    }

    public String getConnectString(String host, String port, String databaseName) {
        String connectionURL = this.jdbcUrlFormat.replace("<host>", host);
        if (StringUtils.isEmpty((String)port)) {
            port = this.defaultPort;
        }
        connectionURL = connectionURL.replace("<port>", port);
        connectionURL = connectionURL.replace("<db>", databaseName);
        return connectionURL;
    }

    public String getCreateDbConnectString(String host, String port, String databaseName) {
        String connectionURL = this.jdbcUrlFormatToCreateDb.replace("<host>", host);
        if (StringUtils.isEmpty((String)port)) {
            port = this.defaultPort;
        }
        connectionURL = connectionURL.replace("<port>", port);
        connectionURL = connectionURL.replace("<db>", databaseName);
        return connectionURL;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public String getJdbcUrlFormat() {
        return this.jdbcUrlFormat;
    }

    public String getDefaultPort() {
        return this.defaultPort;
    }

    public String toString() {
        return this.providerName;
    }

    public String getHibernateConnectionDriverClass() {
        return this.driverClass;
    }

    public String getHibernateDialect() {
        return this.hibernateDialect;
    }

    public static Database getByProviderName(String providerName) {
        Database[] databases;
        for (Database database : databases = Database.values()) {
            if (!database.providerName.equals(providerName)) continue;
            return database;
        }
        return null;
    }
}

