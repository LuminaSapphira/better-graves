package bettergraves.mixin;

import bettergraves.BetterGraves;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(Explosion.class)
public abstract class MixinExplosion {

    BlockPos bettergraves$lastPos = null;

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "net.minecraft.block.BlockState.isAir()Z", ordinal = 0))
    public boolean isAirRedirect(BlockState state) {
        return state.isAir() || BetterGraves.placingGraves.contains(bettergraves$lastPos);
    }


    @Inject(method = "affectWorld", at = @At(value = "INVOKE", target = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void getStateInject(boolean bl, CallbackInfo ci, boolean bl2, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos) {
        bettergraves$lastPos = blockPos;
    }

}
