package bettergraves.client.render;

import bettergraves.block.BetterGraveBE;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GraveRenderer extends BlockEntityRenderer<BetterGraveBE> {
    BlockEntityRenderDispatcher dispatcher;
    public GraveRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.dispatcher = dispatcher;
    }

    @Override
    public void render(BetterGraveBE blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        SkullBlockEntityRenderer.render(null, 50f, SkullBlock.Type.PLAYER, blockEntity.getOwner(), 0f, matrices, vertexConsumers, light);
        matrices.pop();
        matrices.push();
        matrices.translate(0.5, 1.0, 0.5);
        renderLabel(blockEntity.getOwner().getName(), matrices, light, vertexConsumers, blockEntity.getPos());
        matrices.pop();

    }

    private void renderLabel(String string, MatrixStack matrixStack, int light, VertexConsumerProvider vertexConsumerProvider, BlockPos pos) {
        double d = this.dispatcher.camera.getPos().squaredDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
        if (d <= 256.0D) {
            int j = 0;
            matrixStack.push();
            matrixStack.multiply(this.dispatcher.camera.getRotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrixStack.peek().getModel();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int k = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = this.dispatcher.getTextRenderer();
            float h = (float)(-textRenderer.getStringWidth(string) / 2);
            textRenderer.draw(string, h, (float)j, 553648127, false, matrix4f, vertexConsumerProvider, false, k, light);
            textRenderer.draw(string, h, (float)j, -1, false, matrix4f, vertexConsumerProvider, false, 0, light);

            matrixStack.pop();
        }
    }
}
