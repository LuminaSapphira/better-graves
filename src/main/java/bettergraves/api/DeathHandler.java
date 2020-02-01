package bettergraves.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface DeathHandler {

    /**
     * Accepts a player and the killing blow's damage source to determine how to fill the gravestone.
     * @param player The player dying
     * @param deathBlow The DamageSource corresponding to the killing blow
     * @return A Map between Integer and ItemStack that allows you to store items in specific slots.
     *  The Integer is unique to this registered handler and will not conflict with other mods or vanilla.
     */
    ImmutableMap<Integer, ItemStack> handleDeath(ServerPlayerEntity player, DamageSource deathBlow);

}
