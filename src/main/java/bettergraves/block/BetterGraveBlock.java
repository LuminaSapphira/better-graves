package bettergraves.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BetterGraveBlock extends Block implements BlockEntityProvider {

    public BetterGraveBlock() {
        super(FabricBlockSettings.copy(Blocks.BEDROCK).dropsNothing().nonOpaque().luminance(value -> 8));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        assert be instanceof BetterGraveBE;
        if (((BetterGraveBE) be).doesPlayerMatch(player)) {
            if (world.isClient) return ActionResult.SUCCESS;
            BetterGraveBE grave = (BetterGraveBE)be;
            grave.restoreInventory((ServerPlayerEntity)player);
            world.removeBlock(pos, false);
            return ActionResult.SUCCESS;
        } else return ActionResult.FAIL;
    }

    private static VoxelShape plate = VoxelShapes.cuboid(0, 0, 0, 1.0, 0.125, 1.0);
    private static VoxelShape head = VoxelShapes.cuboid(0.25, 0.0, 0.25, 0.75, 0.625, 0.75);
    private static VoxelShape outline = VoxelShapes.union(plate, head);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
        return outline;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BetterGraveBE(pos, state);
    }
}
