package bettergraves.api;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

@FunctionalInterface
public interface RestoreHandler {

    /**
     * Accepts the map previously returned from a DeathHandler to restore items to their appropriate slots.
     * @param player The player restoring to
     * @param items The Map between Integer and ItemStack returned during DeathHandler's execution
     */
    void restoreItems(ServerPlayerEntity player, Map<Integer, ItemStack> items);

}
