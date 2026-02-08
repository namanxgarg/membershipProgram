package com.firstclub.membership.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.MembershipServiceFactory;
import com.firstclub.membership.service.MembershipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MembershipApiServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MembershipApiServlet.class);
    private MembershipService membershipService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            membershipService = MembershipServiceFactory.createMembershipService();
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            logger.info("Membership API Servlet initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize service", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String path = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        try {
            if (path == null || path.equals("/")) {
                sendResponse(resp, 400, createError("Invalid endpoint"));
                return;
            }

            // Public APIs
            if (path.equals("/membership/plans")) {
                try {
                    sendResponse(resp, 200, membershipService.getAvailablePlans());
                } catch (Exception e) {
                    logger.error("Error getting plans", e);
                    sendResponse(resp, 500, createError("Failed to get active plans: " + e.getMessage()));
                }
            } else if (path.equals("/membership/tiers")) {
                sendResponse(resp, 200, membershipService.getAvailableTiers());
            } else if (path.startsWith("/membership/current")) {
                String userId = req.getParameter("userId");
                if (userId == null) {
                    sendResponse(resp, 400, createError("userId parameter required"));
                    return;
                }
                sendResponse(resp, 200, membershipService.getCurrentMembership(userId));
            } else if (path.startsWith("/internal/membership/benefits")) {
                String userId = req.getParameter("userId");
                if (userId == null) {
                    sendResponse(resp, 400, createError("userId parameter required"));
                    return;
                }
                sendResponse(resp, 200, membershipService.getMembershipBenefits(userId));
            } else {
                sendResponse(resp, 404, createError("Endpoint not found: " + path));
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            sendResponse(resp, 500, createError("Internal server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String path = req.getPathInfo();
        String body = readRequestBody(req);

        try {
            if (path == null || path.equals("/")) {
                sendResponse(resp, 400, createError("Invalid endpoint"));
                return;
            }

            if (path.equals("/membership/subscribe")) {
                Map<String, String> request = objectMapper.readValue(body, Map.class);
                String userId = request.get("userId");
                String planId = request.get("planId");
                String tierId = request.get("tierId");
                
                if (userId == null || planId == null || tierId == null) {
                    sendResponse(resp, 400, createError("userId, planId, and tierId required"));
                    return;
                }
                
                var membership = membershipService.subscribe(userId, planId, tierId);
                sendResponse(resp, 201, membership);
                
            } else if (path.equals("/membership/cancel")) {
                Map<String, String> request = objectMapper.readValue(body, Map.class);
                String userId = request.get("userId");
                
                if (userId == null) {
                    sendResponse(resp, 400, createError("userId required"));
                    return;
                }
                
                membershipService.cancelMembership(userId);
                sendResponse(resp, 200, createSuccess("Membership cancelled"));
                
            } else if (path.equals("/internal/membership/order-completed")) {
                Map<String, Object> request = objectMapper.readValue(body, Map.class);
                String userId = (String) request.get("userId");
                Object orderValueObj = request.get("orderValue");
                
                if (userId == null || orderValueObj == null) {
                    sendResponse(resp, 400, createError("userId and orderValue required"));
                    return;
                }
                
                java.math.BigDecimal orderValue = new java.math.BigDecimal(orderValueObj.toString());
                membershipService.onOrderCompleted(userId, orderValue);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Order processed, tier recalculated");
                sendResponse(resp, 200, response);
                
            } else {
                sendResponse(resp, 404, createError("Endpoint not found: " + path));
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            sendResponse(resp, 500, createError("Internal server error: " + e.getMessage()));
        }
    }

    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print(objectMapper.writeValueAsString(data));
        out.flush();
    }

    private Map<String, String> createError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccess(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("success", message);
        return success;
    }
}
