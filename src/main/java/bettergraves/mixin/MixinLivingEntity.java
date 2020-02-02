package bettergraves.mixin;

import bettergraves.mixinimpl.DropDamageSourceTrack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements DropDamageSourceTrack {

    private DamageSource bettergraves$tracked = null;

    @Inject(method = "drop", at = @At("HEAD"))
    private void onDrop(DamageSource source, CallbackInfo info) {
        bettergraves$tracked = source;
    }

    @Override
    public DamageSource bettergraves$getDamageSource() {
        return bettergraves$tracked;
    }
}
