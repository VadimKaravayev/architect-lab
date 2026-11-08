package com.parcelo.events;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanEventProducer {

    public static void main(String[] args) {
        int eventCount = args.length > 0 ? Integer.parseInt(args[0]) : 1;

        try (EventHubProducerClient producer = new EventHubClientBuilder()
                .connectionString(EventHubsConnection.CONNECTION_STRING, EventHubsConnection.HUB_NAME)
                .buildProducerClient()) {

            List<EventData> events = new ArrayList<>();
            for (int i = 0; i < eventCount; i++) {
                String json = scanEventJson();
                events.add(new EventData(json));
                System.out.println("Sending " + json);
            }
            producer.send(events);
            System.out.println("Sent " + eventCount + " event(s) to hub '" + EventHubsConnection.HUB_NAME + "'");
        }
    }

    private static String scanEventJson() {
        String parcelId = "PCL-" + UUID.randomUUID().toString().substring(0, 8);
        return "{\"parcelId\":\"" + parcelId + "\","
                + "\"status\":\"RECEIVED\","
                + "\"depot\":\"DEPOT-NORTH\","
                + "\"scannedAt\":\"" + Instant.now() + "\"}";
    }
}
