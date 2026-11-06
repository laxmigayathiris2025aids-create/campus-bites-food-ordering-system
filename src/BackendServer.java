import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BackendServer {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/test", exchange -> {
            sendResponse(exchange, 200,
                "CAMPUS BITES Backend Connected!");
        });

        server.createContext("/register", exchange -> {

           
            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
            );

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Headers", "Content-Type"
            );

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method not allowed");
                return;
            }

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            Map<String, String> data = parseData(body);

            String registerNumber = data.get("registerNumber");
            String name = data.get("name");
            String email = data.get("email");
            String password = data.get("password");

            if (registerNumber == null || name == null ||
                email == null || password == null ||
                registerNumber.isBlank() || name.isBlank() ||
                email.isBlank() || password.isBlank()) {

                sendResponse(exchange, 400, "All fields are required");
                return;
            }
            String passwordHash;

try {
    passwordHash = PasswordUtil.hashPassword(password);
} catch (Exception e) {
    sendResponse(exchange, 500, "Password hashing failed");
    return;
}

            boolean success = StudentRegistration.registerStudent(
                registerNumber, name, email, passwordHash
            );

            if (success) {
                sendResponse(exchange, 200, "Registration successful!");
            } else {
                sendResponse(exchange, 400,
                    "Registration failed. Check details or duplicate register number.");
            }
        });
        server.createContext("/login", exchange -> {

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Methods", "POST, OPTIONS"
        );
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Headers", "Content-Type"
        );
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        sendResponse(exchange, 405, "Method not allowed");
        return;
    }

    String body = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    Map<String, String> data = parseData(body);

    String email = data.get("email");
    String password = data.get("password");

    if (email == null || password == null ||
        email.isBlank() || password.isBlank()) {
        sendResponse(exchange, 400, "Please enter email and password");
        return;
    }

    boolean success = StudentLogin.loginStudent(email, password);

    if (success) {
        sendResponse(exchange, 200, "Login successful!");
    } else {
        sendResponse(exchange, 401, "Invalid email or password");
    }
});
server.createContext("/checkout", exchange -> {

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        sendResponse(exchange, 405, "Method not allowed");
        return;
    }

    String body = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    Map<String, String> data = parseData(body);

    String registerNumber = data.get("registerNumber");
    String items = data.get("items");

    if (registerNumber == null || registerNumber.isBlank() ||
        items == null || items.isBlank()) {
        sendResponse(exchange, 400, "Register number and cart items are required");
        return;
    }

    Map<Integer, java.util.List<int[]>> shopItems = new HashMap<>();

    try {

        for (String item : items.split(";")) {

            String[] values = item.split(",");

            int foodId = Integer.parseInt(values[0]);
            int shopId = Integer.parseInt(values[1]);
            int price = Integer.parseInt(values[2]);
            int quantity = Integer.parseInt(values[3]);

            if (foodId <= 0 || shopId <= 0 ||
                price < 0 || quantity <= 0) {
                throw new IllegalArgumentException();
            }

            shopItems.computeIfAbsent(
                shopId, key -> new java.util.ArrayList<>()
            ).add(new int[]{foodId, price, quantity});
        }

        int grandTotal = 0;

        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("[");

        boolean firstOrder = true;

        for (Map.Entry<Integer, java.util.List<int[]>> entry
                : shopItems.entrySet()) {

            int shopId = entry.getKey();
            java.util.List<int[]> foodList = entry.getValue();

            int total = 0;

            for (int[] food : foodList) {
                total += food[1] * food[2];
            }

            String referenceCode = "CB" + System.currentTimeMillis();

            int orderId = OrderService.createOrder(
                registerNumber, shopId, referenceCode, total
            );

            if (orderId == -1) {
                sendResponse(exchange, 500, "Order saving failed");
                return;
            }

            for (int[] food : foodList) {

                boolean saved = OrderItemService.addOrderItem(
                    orderId, food[0], food[2], food[1]
                );

                if (!saved) {
                    sendResponse(exchange, 500, "Order item saving failed");
                    return;
                }
            }

            if (!firstOrder) {
                orderDetails.append(",");
            }

            orderDetails.append("{")
                .append("\"shopId\":").append(shopId).append(",")
                .append("\"referenceCode\":\"").append(referenceCode).append("\",")
                .append("\"total\":").append(total)
                .append("}");

            firstOrder = false;
            grandTotal += total;
        }

        orderDetails.append("]");

        String receipt = "{"
            + "\"message\":\"Order placed successfully!\","
            + "\"registerNumber\":\"" + registerNumber + "\","
            + "\"grandTotal\":" + grandTotal + ","
            + "\"orders\":" + orderDetails
            + "}";

        sendResponse(exchange, 200, receipt);

    } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 400, "Invalid order details");
    }
});
server.createContext("/myorders", exchange -> {

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*"
        );
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Methods", "POST, OPTIONS"
        );
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Headers", "Content-Type"
        );
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        sendResponse(exchange, 405, "Method not allowed");
        return;
    }

    String body = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    Map<String, String> data = parseData(body);

    String registerNumber = data.get("registerNumber");

    if (registerNumber == null || registerNumber.isBlank()) {
        sendResponse(exchange, 400, "Register number is required");
        return;
    }

    String result = MyOrders.getOrders(registerNumber);

    sendResponse(exchange, 200, result);
});


        server.start();

        System.out.println(
            "Backend Server running at http://localhost:8080"
        );
    }

    private static Map<String, String> parseData(String body) {

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                data.put(
                    java.net.URLDecoder.decode(parts[0],
                        StandardCharsets.UTF_8),
                    java.net.URLDecoder.decode(parts[1],
                        StandardCharsets.UTF_8)
                );
            }
        }

        return data;
    }

    private static void sendResponse(
        HttpExchange exchange, int status, String message
    ) throws IOException {

        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().add(
            "Content-Type", "text/plain; charset=UTF-8"
        );

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}