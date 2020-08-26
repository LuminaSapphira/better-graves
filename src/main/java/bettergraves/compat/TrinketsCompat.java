package bettergraves.compat;

import bettergraves.api.BetterGravesAPI;
import com.google.common.collect.ImmutableMap;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class TrinketsCompat {

    public static void register() {
        BetterGravesAPI.registerDeathHandler("bg-trinkets", (player, source) -> {
            ImmutableMap.Builder<Integer, ItemStack> map = ImmutableMap.builder();
            Inventory trinketsInv = TrinketsApi.getTrinketsInventory(player);
            for (int i = 0; i < trinketsInv.size(); ++i) {
                map.put(i, trinketsInv.getStack(i));
            }
            trinketsInv.clear();
            return map.build();
        }, (player, map) -> {
            SetStackHelper inv = new SetStackHelper(TrinketsApi.getTrinketsInventory(player), player);
            map.forEach(inv::setStackOrDrop);
        });
    }

    private static class SetStackHelper {
        private final Inventory inv;
        private final ServerPlayerEntity player;
        public SetStackHelper(Inventory inv, ServerPlayerEntity player) {
            this.inv = inv;
            this.player = player;
        }
        public void setStackOrDrop(int i, ItemStack stack) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, stack);
            } else {
                player.inventory.offerOrDrop(player.world, stack);
            }
        }
    }

}
