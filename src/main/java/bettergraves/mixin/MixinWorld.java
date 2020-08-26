package bettergraves.mixin;

import bettergraves.BetterGraves;
import bettergraves.block.BetterGraveBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos pos);

    // There's no way this could *possibly* have unintended consequences.
    @Inject(at = @At("HEAD"), method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", cancellable = true)
    private void setBlockStateCb(BlockPos pos, BlockState newState, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getBlockState(pos).isAir() && this.getBlockState(pos).getBlock() == BetterGraves.BETTER_GRAVE_BLOCK && newState.isAir()) {
            BlockEntity be = this.getBlockEntity(pos);
            if (be instanceof BetterGraveBE) {
                BetterGraveBE grave = (BetterGraveBE)be;
                if (!grave.isRestored()) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
