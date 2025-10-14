package com.libronova.config;

import com.libronova.errors.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final String PROPERTIES_FILE = "db.properties";

    private static Properties loadProperties() throws DataAccessException {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new DataAccessException("Cannot find " + PROPERTIES_FILE + " in resources folder");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new DataAccessException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws DataAccessException {
        try {
            Properties props = loadProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database", e);
        }
    }
}