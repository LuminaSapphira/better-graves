package bettergraves.api;

import bettergraves.BetterGraves;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.Level;

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
     * This should not be used externally
     */
    public static Map<String, GravePreStoreHandler> preStoreHandlers = new HashMap<>();

    /**
     * This should not be used externally
     */
    public static Map<String, GraveStoreHandler> storeHandlers = new HashMap<>();

    /**
     * This should not be used externally
     */
    public static Map<String, GravePlacementHandler> gravePlacementHandlers = new HashMap<>();

    /**
     * Registers a DeathHandler and RestoreHandler pair with a String key to identify the pair.
     * @param key The identifier for this pair of handlers. Recommended to be your mod id.
     * @param deathHandler The DeathHandler that serializes your mod's drops into a Map between Integer and ItemStack
     * @param restoreHandler The RestoreHandler that deserializes the previously provided Map
     */
    public static void registerDeathHandler(String key, DeathHandler deathHandler, RestoreHandler restoreHandler) {
        BetterGraves.log(Level.INFO, "Registering handler " + key);
        if (deathHandlers.containsKey(key)) {
            throw new CrashException(
                    CrashReport.create(new RuntimeException("Duplicate key"),
                            "Duplicate DeathHandler key " + key));
        }
        deathHandlers.put(key, deathHandler);
        restoreHandlers.put(key, restoreHandler);
    }

    public static void registerCustomGraveHandler(String key, GravePreStoreHandler preStoreHandler, GraveStoreHandler storeHandler, GravePlacementHandler placementHandler) {
        BetterGraves.log(Level.INFO, "Registering custom grave logic for " + key);
        if (preStoreHandlers.containsKey(key)) {
            throw new CrashException(
                    CrashReport.create(new RuntimeException("Duplicate key"),
                            "Duplicate custom grave handler key " + key)
            );
        }
        preStoreHandlers.put(key, preStoreHandler);
        storeHandlers.put(key, storeHandler);
        gravePlacementHandlers.put(key, placementHandler);
    }

}
