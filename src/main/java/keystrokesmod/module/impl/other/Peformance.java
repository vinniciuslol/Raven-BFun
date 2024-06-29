package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

public class Peformance extends Module {

    public Peformance() {
        super("Performance", category.other);
        optimizeRendering();
    }

    private void optimizeRendering() {
        GlStateManager.disableFog();
        Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
        Minecraft.getMinecraft().gameSettings.useVbo = true;
        Minecraft.getMinecraft().guiAchievement.clearAchievements();
        Minecraft.getMinecraft().gameSettings.entityShadows = false;
    }



}
