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

            // Read entire script
            Scanner scanner = new Scanner(input).useDelimiter("\\A");
            String scriptContent = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            
            // Split by semicolon (handle both with and without newlines)
            String[] statements = scriptContent.split(";");
            Statement stmt = connection.createStatement();
            int executedCount = 0;

            for (String sql : statements) {
                sql = sql.trim();
                // Skip empty lines and full-line comments
                if (sql.isEmpty() || sql.matches("^\\s*$") || sql.matches("^\\s*--.*$")) {
                    continue;
                }
                
                // Remove inline comments (-- to end of line)
                sql = sql.replaceAll("--[^\r\n]*", "").trim();
                if (sql.isEmpty()) {
                    continue;
                }
                
                try {
                    boolean hasResult = stmt.execute(sql);
                    executedCount++;
                    System.out.println("✓ Executed statement " + executedCount + " from " + scriptPath);
                } catch (SQLException e) {
                    String errorMsg = e.getMessage();
                    // Ignore errors for existing objects
                    if (errorMsg != null && 
                        (errorMsg.contains("already exists") || 
                         errorMsg.contains("duplicate key") ||
                         (errorMsg.contains("constraint") && errorMsg.contains("already exists")))) {
                        // Expected for idempotent scripts
                        System.out.println("  (skipped - already exists)");
                    } else {
                        System.err.println("✗ SQL Error in " + scriptPath + ": " + errorMsg);
                        System.err.println("  Statement: " + sql.substring(0, Math.min(200, sql.length())));
                        e.printStackTrace();
                    }
                }
            }
            stmt.close();
            System.out.println("Script " + scriptPath + " completed - executed " + executedCount + " statements");
        } catch (Exception e) {
            System.err.println("Error executing script " + scriptPath + ": " + e.getMessage());
            e.printStackTrace();
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
