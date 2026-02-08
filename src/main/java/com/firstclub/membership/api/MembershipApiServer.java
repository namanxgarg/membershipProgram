package com.firstclub.membership.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MembershipApiServer {
    private static final Logger logger = LoggerFactory.getLogger(MembershipApiServer.class);
    private Server server;
    private int port;

    public MembershipApiServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        server = new Server(port);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register API servlet
        ServletHolder apiHolder = new ServletHolder(MembershipApiServlet.class);
        context.addServlet(apiHolder, "/api/*");

        server.start();
        logger.info("API Server started on port {}", port);
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    public void join() throws InterruptedException {
        if (server != null) {
            server.join();
        }
    }
}
