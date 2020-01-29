package bettergraves.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BetterGraveBlock extends BlockWithEntity {

    public BetterGraveBlock() {
        super(FabricBlockSettings.copy(Blocks.BEDROCK).lightLevel(8).build());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }
}
