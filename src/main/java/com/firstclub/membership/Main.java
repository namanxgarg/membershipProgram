package com.firstclub.membership;

import com.firstclub.membership.api.MembershipApiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting FirstClub Membership Service...");
            
            com.firstclub.membership.db.DatabaseConnection.getInstance();
            
            int port = getPort();
            MembershipApiServer server = new MembershipApiServer(port);
            server.start();
            
            logger.info("Membership Service started on port {}", port);
            logger.info("API available at http://localhost:{}/api", port);
            
            server.join();
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }

    private static int getPort() {
        String port = System.getProperty("server.port");
        if (port == null) {
            port = System.getenv("SERVER_PORT");
        }
        if (port == null) {
            return 8080;
        }
        return Integer.parseInt(port);
    }
}
