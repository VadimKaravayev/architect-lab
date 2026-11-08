package com.fake.downstream;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <serviceName> <port> [responseDelayMs]");
            return;
        }
        String serviceName = args[0];
        int port = Integer.parseInt(args[1]);
        long responseDelayMs = args.length > 2 ? Long.parseLong(args[2]) : 0;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/scan", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            System.out.println("Method: " + requestMethod);
            if (!"POST".equals(requestMethod)) {
                exchange.sendResponseHeaders(405, -1);   // 405 Method Not Allowed, -1 = no body
                exchange.close();
                return;
            }
            try { Thread.sleep(responseDelayMs); } catch (InterruptedException ignored) {}
            byte[] body = (serviceName + " OK\n").getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println(serviceName + " listening on " + port + " (delay " + responseDelayMs + " ms)");
    }
}
