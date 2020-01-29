package bettergraves;

import bettergraves.block.BetterGraveBE;
import bettergraves.block.BetterGraveBlock;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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

}
