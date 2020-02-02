package bettergraves.compat;

import bettergraves.api.BetterGravesAPI;
import com.google.common.collect.ImmutableMap;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class TrinketsCompat {

    public static void register() {
        BetterGravesAPI.registerDeathHandler("trinkets", (player, source) -> {
            ImmutableMap.Builder<Integer, ItemStack> map = ImmutableMap.builder();
            Inventory trinketsInv = TrinketsApi.getTrinketsInventory(player);
            for (int i = 0; i < trinketsInv.getInvSize(); ++i) {
                map.put(i, trinketsInv.getInvStack(i));
            }
            trinketsInv.clear();
            return map.build();
        }, (player, map) -> {
            Inventory inv = TrinketsApi.getTrinketsInventory(player);
            map.forEach(inv::setInvStack);
        });
    }

}