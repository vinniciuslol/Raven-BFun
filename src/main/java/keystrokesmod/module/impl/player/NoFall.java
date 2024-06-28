package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NoFall extends Module {
    public SliderSetting mode;
    private SliderSetting minFallDistance;
    private ButtonSetting disableAdventure;
    private ButtonSetting ignoreVoid;

    private String[] modes = new String[]{"Spoof", "Extra", "NoGround", "Blink"};

    private Queue<Packet> packets = new ConcurrentLinkedQueue<>();
    private Queue<Packet> nofallPackets = new ConcurrentLinkedQueue<>();
    private boolean blinking;
    private int ticks = 0;

    public NoFall() {
        super("NoFall", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3.0, 0.0, 8.0, 0.1));
        this.registerSetting(disableAdventure = new ButtonSetting("Disable adventure", false));
        this.registerSetting(ignoreVoid = new ButtonSetting("Ignore void", true));
	}

    @Override
    public void onDisable() {
        ticks = 0;
        blinking = false;
        resetPackets();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (mc.thePlayer == null)
            return;

        if (mode.getInput() != 3)
            return;

        if (!blinking || ticks <= 1 || mc.currentScreen == null) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        String text = "blinking: §";
        if (ticks > 50) {
            text += "c";
        } else if (ticks > 30) {
            text += "6";
        } else if (ticks > 20) {
            text += "e";
        } else {
            text += "a";
        }

        text += ticks;
        int[] disp = new int[]{scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight()};
        int wid = mc.fontRendererObj.getStringWidth(text) / 2 - 2;
        mc.fontRendererObj.drawString(text, (float) disp[0] / 2 - wid, (float) disp[1] / 2 + 13, -1, true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreMotion(PreMotionEvent e) {
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }
        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }
		
        if ((double) mc.thePlayer.fallDistance > minFallDistance.getInput() || minFallDistance.getInput() == 0) {
            switch ((int) mode.getInput()) {
                case 0:
                    e.setOnGround(true);
                    break;
                case 2:
                    e.setOnGround(false);
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (mode.getInput() != 3)
            return;

		if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }
        if (ignoreVoid.isToggled() && isVoid()) {
            resetPackets();
            return;
        }
		
        ++ticks;
		
		if (blinking) {
            if (mc.thePlayer.fallDistance <= minFallDistance.getInput()) {
                if (mc.thePlayer.onGround) {
                    resetPackets();
                }
            }
        } else {
            if (!isBlockUnder())
                blinking = true;
        }
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }

        if (ignoreVoid.isToggled() && isVoid()) {
            resetPackets();
            return;
        }

        if (mode.getInput() != 3 && !blinking)
            return;

        if (e.getPacket() instanceof C03PacketPlayer) {
            nofallPackets.add(new C03PacketPlayer(true));
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition c04 = (C03PacketPlayer.C04PacketPlayerPosition) e.getPacket();

            nofallPackets.add(new C03PacketPlayer.C04PacketPlayerPosition(c04.getPositionX(), c04.getPositionY(), c04.getPositionZ(), true));
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook c05 = (C03PacketPlayer.C05PacketPlayerLook) e.getPacket();

            nofallPackets.add(new C03PacketPlayer.C05PacketPlayerLook(c05.getYaw(), c05.getPitch(), true));
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook c06 = (C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket();

            nofallPackets.add(new C03PacketPlayer.C06PacketPlayerPosLook(c06.getPositionX(), c06.getPositionY(), c06.getPositionZ(), c06.getYaw(), c06.getPitch(), true));
            e.setCanceled(true);
        }

        nofallPackets.add(e.getPacket());
        packets.add(e.getPacket());
        e.setCanceled(true);
    }

    public void onUpdate() {
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }
        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }
        if (mode.getInput() == 1 && ((double)mc.thePlayer.fallDistance > minFallDistance.getInput()|| minFallDistance.getInput() == 0)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        }
    }

    private void resetPackets() {
        synchronized (nofallPackets) {
            if (!nofallPackets.isEmpty()) {
                nofallPackets.forEach(PacketUtils::sendPacketNoEvent);
                nofallPackets.clear();
            }
        }

        synchronized (packets) {
            if (!packets.isEmpty()) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
            }
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isBlockUnder() {
        for(int y = (int) mc.thePlayer.posY; y >= 0; y--) {
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }

    private boolean isVoid() {
        return mc.thePlayer != null && Utils.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

}
