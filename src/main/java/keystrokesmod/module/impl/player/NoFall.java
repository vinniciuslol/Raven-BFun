package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
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
    private int ticks = 0, maxFall = 30;
    private boolean blinking;

    public NoFall() {
        super("NoFall", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3.0, 0.0, 8.0, 0.1));
        this.registerSetting(disableAdventure = new ButtonSetting("Disable adventure", false));
        this.registerSetting(ignoreVoid = new ButtonSetting("Ignore void", true));
	}

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        if (mode.getInput() != 3)
            return;

        if (!blinking || ticks <= 1) {
            return;
        }

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
        mc.fontRendererObj.drawString(text, disp[0] / 2 - wid, disp[1] / 2 + 13, -1, true);
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

        ++ticks;

        if (mc.thePlayer.isDead || mc.thePlayer.fallDistance > maxFall || mc.thePlayer.capabilities.isFlying || mc.thePlayer.hurtTime != 0 || mc.thePlayer.onGround && !Utils.onEdge()) {
            if (mc.thePlayer.onGround && !mc.thePlayer.capabilities.allowFlying && mc.thePlayer.hurtTime == 0) {
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

            blinking = false;
            ticks = 0;
        }
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }

        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }

        if (!blinking || mode.getInput() != 3)
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
        nofallPackets.add(e.getPacket());
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

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isVoid() {
        return Utils.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
}
