package com.parcelo.events;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;

import java.time.Duration;

public class PartitionPeek {

    public static void main(String[] args) {
        String partitionId = args.length > 0 ? args[0] : "0";

        try (EventHubConsumerClient consumer = new EventHubClientBuilder()
                .connectionString(EventHubsConnection.CONNECTION_STRING, EventHubsConnection.HUB_NAME)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .buildConsumerClient()) {

            int count = 0;
            for (PartitionEvent partitionEvent : consumer.receiveFromPartition(
                    partitionId, 100, EventPosition.earliest(), Duration.ofSeconds(5))) {
                EventData event = partitionEvent.getData();
                System.out.printf("partition=%s seq=%d offset=%s enqueued=%s body=%s%n",
                        partitionEvent.getPartitionContext().getPartitionId(),
                        event.getSequenceNumber(),
                        event.getOffsetString(),
                        event.getEnqueuedTime(),
                        event.getBodyAsString());
                count++;
            }
            System.out.println("Read " + count + " event(s) from partition " + partitionId);
        }
    }
}
