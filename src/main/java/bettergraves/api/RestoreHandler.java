package bettergraves.api;

import net.minecraft.item.ItemStack;

import java.util.Map;

@FunctionalInterface
public interface RestoreHandler {

    void restoreItems(Map<Integer, ItemStack> items);

}
