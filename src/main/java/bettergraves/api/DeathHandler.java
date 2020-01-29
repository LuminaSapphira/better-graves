package bettergraves.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface DeathHandler {

    ImmutableMap<Integer, ItemStack> handleDeath(ServerPlayerEntity player);

}
