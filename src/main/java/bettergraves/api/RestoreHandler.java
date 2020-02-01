package bettergraves.api;

import net.minecraft.item.ItemStack;

import java.util.Map;

@FunctionalInterface
public interface RestoreHandler {

    /**
     * Accepts the map previously returned from a DeathHandler to restore items to their appropriate slots.
     * @param items The Map between Integer and ItemStack returned during DeathHandler's execution
     */
    void restoreItems(Map<Integer, ItemStack> items);

}
