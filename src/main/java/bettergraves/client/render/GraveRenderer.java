package bettergraves.client.render;

import bettergraves.BetterGraves;
import bettergraves.block.BetterGraveBE;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

public class GraveRenderer implements BlockEntityRenderer<BetterGraveBE> {
    private BlockEntityRendererFactory.Context context;
    private SkullBlockEntityModel headModel;

    public GraveRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
        this.headModel = SkullBlockEntityRenderer.getModels(ctx.getLayerRenderDispatcher()).get(SkullBlock.Type.PLAYER);
    }

    @Override
    public void render(BetterGraveBE blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity == null) return;
        else if (blockEntity.getOwner() == null) {
            BetterGraves.log(Level.ERROR, "Grave BE Owner is null, cannot render");
            return;
        }
        else if (blockEntity.getOwner().getName() == null) {
            BetterGraves.log(Level.ERROR, "Grave BE Owner name is null, cannot render");
            return;
        }
        matrices.push();
        matrices.translate(0.0, 0.125, 0.0);

        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.PLAYER, blockEntity.getOwner());
        SkullBlockEntityRenderer.renderSkull(null, 0f, 0f, matrices, vertexConsumers, light, headModel, renderLayer);
        matrices.pop();
        matrices.push();
        matrices.translate(0.5, 1.0, 0.5);
        renderLabel(blockEntity.getOwner().getName(), matrices, light, vertexConsumers, blockEntity.getPos());
        matrices.pop();

    }

    private void renderLabel(String string, MatrixStack matrixStack, int light, VertexConsumerProvider vertexConsumerProvider, BlockPos pos) {
        double d = this.context.getRenderDispatcher().camera.getPos().squaredDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
        if (d <= 256.0D) {
            int j = 0;
            matrixStack.push();
            matrixStack.multiply(this.context.getRenderDispatcher().camera.getRotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int k = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = this.context.getTextRenderer();
            float h = (float)(-textRenderer.getWidth(string) / 2);
            textRenderer.draw(string, h, (float)j, 553648127, false, matrix4f, vertexConsumerProvider, false, k, light);
            textRenderer.draw(string, h, (float)j, -1, false, matrix4f, vertexConsumerProvider, false, 0, light);

            matrixStack.pop();
        }
    }
}
