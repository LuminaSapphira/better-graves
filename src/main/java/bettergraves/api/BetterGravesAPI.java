package bettergraves.api;

import java.util.HashMap;
import java.util.Map;

public class BetterGravesAPI {

    public static Map<String, DeathHandler> deathHandlers = new HashMap<>();
    public static Map<String, RestoreHandler> restoreHandlers = new HashMap<>();

    public void registerDeathHandler(String key, DeathHandler handler, RestoreHandler restoreHandler) {
        deathHandlers.put(key, handler);
        restoreHandlers.put(key, restoreHandler);
    }

}
