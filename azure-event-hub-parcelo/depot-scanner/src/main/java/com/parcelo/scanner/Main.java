package com.parcelo.scanner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class Main {

    private static final List<URI> DOWNSTREAM_SERVICES = List.of(
            URI.create("http://localhost:8081/scan"),   // tracking
            URI.create("http://localhost:8082/scan"),   // billing
            URI.create("http://localhost:8083/scan")    // analytics
    );

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public static void main(String[] args) {
        int scanCount = args.length > 0 ? Integer.parseInt(args[0]) : 1;

        for (int i = 1; i <= scanCount; i++) {
            String parcelId = "PCL-" + i;
            long start = System.currentTimeMillis();
            try {
                sendScan(parcelId);
                System.out.println(parcelId + " OK in " + (System.currentTimeMillis() - start) + " ms");
            } catch (Exception e) {
                System.out.println(parcelId + " FAILED in " + (System.currentTimeMillis() - start) + " ms: " + e);
            }
        }
    }

    private static void sendScan(String parcelId) throws Exception {
        String body = "{\"parcelId\":\"" + parcelId + "\",\"status\":\"RECEIVED\"}";
        for (URI service : DOWNSTREAM_SERVICES) {
            HttpRequest request = HttpRequest.newBuilder(service)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException(service + " returned " + response.statusCode());
            }
        }
    }
}
