package bettergraves;

import bettergraves.api.BetterGravesAPI;
import bettergraves.block.BetterGraveBE;
import bettergraves.block.BetterGraveBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterGraves implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bettergraves";
    public static final String MOD_NAME = "Better Graves";

    public static BlockEntityType<BetterGraveBE> BETTER_GRAVE_BE_TYPE;
    public static final BetterGraveBlock BETTER_GRAVE_BLOCK = new BetterGraveBlock();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "better_grave"), BETTER_GRAVE_BLOCK);
        BETTER_GRAVE_BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "better_grave"), BlockEntityType.Builder.create(BetterGraveBE::new, BETTER_GRAVE_BLOCK).build(null));
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    private static BlockPos gravePos(BlockPos deathLocation, ServerWorld world) {
        BlockPos adjusted = deathLocation;
        if (deathLocation.getY() < 0 || deathLocation.getY() > world.getHeight()) {
            adjusted = new BlockPos(adjusted.getX(), MathHelper.clamp(adjusted.getY(), 0, 255), adjusted.getZ());
        }
        if (!world.getBlockState(adjusted).isAir()) {
            adjusted = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, adjusted);
        } else if (world.getBlockState(adjusted.down()).isAir()) {
            adjusted = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, adjusted);
        }
        if (adjusted.getY() < 0 || adjusted.getY() > world.getHeight() && world.getBlockState(deathLocation).getHardness(world, deathLocation) >= 0) {
            return deathLocation;
        }
        return adjusted;

    }

    public static void placeGrave(BlockPos deathLocation, ServerPlayerEntity player, ServerWorld world, DamageSource source) {
        BetterGraveBE grave = new BetterGraveBE();
        BetterGravesAPI.deathHandlers.forEach((key, handler) -> grave.storeInventory(key, handler.handleDeath(player, source)));
        grave.storeInventory(player.inventory);
        new Thread(() -> {
            try {
                Thread.sleep(100, 0);
                world.getServer().execute(() -> {
                    BlockPos pos = gravePos(deathLocation, world);
                    world.setBlockState(pos, BETTER_GRAVE_BLOCK.getDefaultState());
                    world.setBlockEntity(pos, grave);
                });
            } catch (InterruptedException e) {
                throw new CrashException(CrashReport.create(e, "Placing grave"));
            }
        }).start();
    }


}
