package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PlayerCircle extends Module {
    public static SliderSetting theme;
    public static SliderSetting thickness;

    public PlayerCircle() {
        super("PlayerCircle", category.render);
        this.registerSetting(theme = new SliderSetting("Color Theme", Theme.themes, 1));
        this.registerSetting(thickness = new SliderSetting("Thickness", 1.0, 1.0, 5.0, 0.1));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!this.isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        Entity target = ModuleManager.killAura.target;
        if (target != null) {
            renderCircleAroundTarget(target, event.partialTicks);
        }
    }



    private void renderCircleAroundTarget(Entity target, float partialTicks) {
        double radius = 1.0;
        int segments = 50;

        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;

        Minecraft mc = Minecraft.getMinecraft();
        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        GL11.glTranslated(x - viewerX, y - viewerY, z - viewerZ);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        int themeIndex = (int) theme.getInput();
        int color = Theme.getGradient(themeIndex, 0);


        float red = (float) ((color >> 16) & 0xFF) / 255.0f;
        float green = (float) ((color >> 8) & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;

        GL11.glColor4f(red, green, blue, 1.0f);

        GL11.glLineWidth((float) thickness.getInput());

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double dx = radius * Math.cos(angle);
            double dz = radius * Math.sin(angle);
            worldRenderer.pos(dx, 0, dz).endVertex();
        }
        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }// -----
}
