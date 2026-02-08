package com.firstclub.membership.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String driver;

    private DatabaseConnection() {
        loadProperties();
        initializeDatabase();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void loadProperties() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties");
            
            if (input == null) {
                this.url = "jdbc:h2:mem:membershipdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
                this.driver = "org.h2.Driver";
                this.username = "sa";
                this.password = "";
            } else {
                props.load(input);
                this.url = props.getProperty("db.url", 
                    "jdbc:h2:mem:membershipdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
                this.driver = props.getProperty("db.driver", "org.h2.Driver");
                this.username = props.getProperty("db.username", "sa");
                this.password = props.getProperty("db.password", "");
            }
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            this.url = "jdbc:h2:mem:membershipdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
            this.driver = "org.h2.Driver";
            this.username = "sa";
            this.password = "";
        }
    }

    private void initializeDatabase() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            
            executeScript("db/schema.sql");
            executeScript("db/data.sql");
            
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeScript(String scriptPath) {
        try {
            InputStream input = getClass().getClassLoader()
                .getResourceAsStream(scriptPath);
            
            if (input == null) {
                System.err.println("Script not found: " + scriptPath);
                return;
            }

            Scanner scanner = new Scanner(input).useDelimiter(";");
            Statement stmt = connection.createStatement();

            while (scanner.hasNext()) {
                String sql = scanner.next().trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("already exists") && 
                            !e.getMessage().contains("duplicate")) {
                            System.err.println("SQL Error: " + e.getMessage());
                        }
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error executing script " + scriptPath + ": " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (connection == null) {
                throw new SQLException("Database not initialized. Connection is null.");
            }
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
