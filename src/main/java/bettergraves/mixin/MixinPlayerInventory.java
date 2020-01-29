package bettergraves.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {

    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    /**
     * Overwritten to add slot-aware compatibility to Better Graves
     * @author CerulanLumina
     */
    @Overwrite
    public void dropAll() {

        this.combinedInventory.forEach(DefaultedList::clear);
    }

}
