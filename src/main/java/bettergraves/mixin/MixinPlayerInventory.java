package bettergraves.mixin;

import bettergraves.BetterGraves;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {

    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow @Final public PlayerEntity player;

    /**
     * @author CerulanLumina
     * @reason Overwritten to add slot-aware compatibility to Better Graves
     */
    @Overwrite
    public void dropAll() {
        if (player.world.isClient) return;
        ServerPlayerEntity player = (ServerPlayerEntity)this.player;
        BetterGraves.placeGrave(player.getBlockPos(), player, player.getServerWorld());
        this.combinedInventory.forEach(DefaultedList::clear);
    }

}
