package bettergraves.api;

import java.util.HashMap;
import java.util.Map;

public class BetterGravesAPI {

    /**
     * This should not be used externally
     */
    public static Map<String, DeathHandler> deathHandlers = new HashMap<>();

    /**
     * This should not be used externally
     */
    public static Map<String, RestoreHandler> restoreHandlers = new HashMap<>();

    /**
     * Registers a DeathHandler and RestoreHandler pair with a String key to identify the pair.
     * @param key The identifier for this pair of handlers. Recommended to be your mod id.
     * @param handler The DeathHandler that serializes your mod's drops into a Map between Integer and ItemStack
     * @param restoreHandler The RestoreHandler that deserializes the previously provided Map
     */
    public void registerDeathHandler(String key, DeathHandler handler, RestoreHandler restoreHandler) {
        deathHandlers.put(key, handler);
        restoreHandlers.put(key, restoreHandler);
    }

}
