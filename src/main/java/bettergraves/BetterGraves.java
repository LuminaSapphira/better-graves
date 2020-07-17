package bettergraves;

import bettergraves.api.BetterGravesAPI;
import bettergraves.api.DeathHandler;
import bettergraves.api.RestoreHandler;
import bettergraves.block.BetterGraveBE;
import bettergraves.block.BetterGraveBlock;
import bettergraves.compat.TrinketsCompat;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BetterGraves implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bettergraves";
    public static final String MOD_NAME = "Better Graves";

    public static BlockEntityType<BetterGraveBE> BETTER_GRAVE_BE_TYPE;
    public static final BetterGraveBlock BETTER_GRAVE_BLOCK = new BetterGraveBlock();

    public static BGConfig config = null;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "better_grave"), BETTER_GRAVE_BLOCK);
        config = BGConfig.getConfig(FabricLoader.getInstance().getConfigDirectory().toPath());
        BETTER_GRAVE_BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "better_grave"), BlockEntityType.Builder.create(BetterGraveBE::new, BETTER_GRAVE_BLOCK).build(null));
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsCompat.register();
        }
        ServerTickCallback.EVENT.register(server -> {
            placingGraves.clear();
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    private static BlockPos gravePos(BlockPos deathLocation, ServerWorld world) {
        BlockPos adjusted = deathLocation;
        // clamp the pos to inside the world
        if (deathLocation.getY() < 0 || deathLocation.getY() > world.getHeight()) {
            adjusted = new BlockPos(adjusted.getX(), MathHelper.clamp(adjusted.getY(), 0,  world.getDimensionHeight()), adjusted.getZ());
        }

        boolean found = false;
        // if that pos is not air, find the next air above it within the world
        if (!world.getBlockState(adjusted).isAir()) {

            for (int i = 0; i < world.getDimensionHeight() - adjusted.getY(); ++i) {
                if (world.getBlockState(adjusted.offset(Direction.UP, i)).isAir()) {
                    adjusted = adjusted.offset(Direction.UP, i);
                    found = true;
                    break;
                }
            }

        }

        if (!found) {
            return deathLocation;
        }
        return adjusted;

    }

    public static HashSet<BlockPos> placingGraves = new HashSet<>();

    public static void placeGrave(BlockPos deathLocation, ServerPlayerEntity player, ServerWorld world, DamageSource deathBlow) {
        // Create an orphaned Grave BlockEntity to store inventory in prior to grave placement
        BetterGraveBE grave = new BetterGraveBE();

        // API : Pre-store handlers - determines whether we should use their logic for graves
        List<String> keys = BetterGravesAPI.preStoreHandlers.entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue().beforeGraveStore(world, deathLocation, player, deathBlow))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // If a pre-store handler returned true (should use their logic), do so, otherwise, use standard logic
        Optional<String> graveKey = Optional.empty();
        if (keys.size() > 0) {
            if (keys.size() > 1)
                log(Level.WARN, "Duplicate grave handlers for a given context! Only using first. [" + String.join(",", keys) + "]");
            graveKey = Optional.of(keys.get(0));
            ImmutableMap.Builder<String, Pair<DeathHandler, RestoreHandler>> mapBuilder = ImmutableMap.builder();
            BetterGravesAPI.deathHandlers.forEach((key, handler) -> mapBuilder.put(key, new Pair<>(handler, BetterGravesAPI.restoreHandlers.get(key))));
            BetterGravesAPI.storeHandlers.get(graveKey.get()).store(world, deathLocation, player, deathBlow, mapBuilder.build());

        } else {
            BetterGravesAPI.deathHandlers.forEach((key, handler) -> grave.storeInventory(key, handler.handleDeath(player, deathBlow)));
            grave.storeInventory(player.inventory);
        }

        BlockPos pos = gravePos(deathLocation, world);
        // If any pre-store execute its placement handler
        // else normal logic
        if (graveKey.isPresent()) {
            String key = graveKey.get();
            BetterGravesAPI.gravePlacementHandlers.get(key).place(world, pos);
        } else {
            placingGraves.add(pos);
            world.setBlockState(pos, BETTER_GRAVE_BLOCK.getDefaultState());
            world.setBlockEntity(pos, grave);
        }
    }

}
