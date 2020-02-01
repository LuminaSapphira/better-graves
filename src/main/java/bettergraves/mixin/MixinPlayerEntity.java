package bettergraves.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class MixinServerPlayerEntity {

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", ordinal = 4))
    public void dropAll(PlayerInventory inventory) {
        
    }

}
