package bettergraves.mixin;

import bettergraves.BetterGraves;
import bettergraves.mixinimpl.DropDamageSourceTrack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"), require = 1)
    private void dropAll(PlayerInventory inventory) {
        if (this.world.isClient) return;
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        BetterGraves.placeGrave(player.getBlockPos(), player, player.getWorld(), ((DropDamageSourceTrack)player).bettergraves$getDamageSource());
        inventory.clear();
    }

}
