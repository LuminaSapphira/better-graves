package bettergraves.api;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface GravePreStoreHandler {

    /**
     * Accepts the world, location, player, and killing blow of a death to determine if your mod's custom logic should
     * execute. Return true to execute your mod's logic, otherwise false.
     * @param deathWorld The world the player is dying in
     * @param deathLocation The location of the death - not the location of the grave
     * @param deadPlayer The dying player
     * @param deathBlow The killing blow
     * @return True if Better Graves' logic should be replaced with your own in this circumstance.
     */
    boolean beforeGraveStore(World deathWorld, BlockPos deathLocation, ServerPlayerEntity deadPlayer, DamageSource deathBlow);

}
