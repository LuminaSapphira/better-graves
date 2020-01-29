package bettergraves.client;

import bettergraves.BetterGraves;
import bettergraves.client.render.GraveRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import org.apache.logging.log4j.Level;

public class BetterGravesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BetterGraves.log(Level.INFO, "Client Initializing");
        BlockEntityRendererRegistry.INSTANCE.register(BetterGraves.BETTER_GRAVE_BE_TYPE, GraveRenderer::new);

    }
}
