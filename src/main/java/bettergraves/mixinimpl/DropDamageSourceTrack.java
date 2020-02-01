package bettergraves.mixinimpl;

import net.minecraft.entity.damage.DamageSource;

import javax.annotation.Nullable;

public interface DropDamageSourceTrack {

    @Nullable
    DamageSource bettergraves$getDamageSource();

}
