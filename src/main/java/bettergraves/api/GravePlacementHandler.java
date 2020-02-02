package bettergraves.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface GravePlacementHandler {

    /**
     * Actually place the grave
     * @param world The world to place the grave in
     * @param gravePos The calculated position of the grave
     */
    void place(World world, BlockPos gravePos);

}
