package bettergraves.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface GraveStoreHandler {

    /**
     * Store the contents of the grave. Passes the player that is dying and all registered death handlers.
     * You are responsible for storing the player's inventory and executing the third-party handlers at the appropriate
     * times.
     * @param world The world the player is dying in
     * @param deathPos The death location - not the grave location
     * @param deadPlayer - The player that is dying
     * @param deathBlow - The killing blow
     * @param modHandlers - The registered mod handlers - mapped from key to a pair of the store/restore handlers
     */
    void store(World world, BlockPos deathPos, ServerPlayerEntity deadPlayer, DamageSource deathBlow, ImmutableMap<String, Pair<DeathHandler, RestoreHandler>> modHandlers);

}
