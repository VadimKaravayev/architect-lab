package com.parcelo.events;

public final class EventHubsConnection {
    private static final String EMULATOR =
            "Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;"
            + "SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;";

    // Real Azure: export EVENTHUBS_CONNECTION_STRING=... ; unset = local emulator.
    public static final String CONNECTION_STRING =
            System.getenv().getOrDefault("EVENTHUBS_CONNECTION_STRING", EMULATOR);
    public static final String HUB_NAME = "parcels";

    private EventHubsConnection() {}
}
