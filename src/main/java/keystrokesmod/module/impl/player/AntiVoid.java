package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedList;

public class AntiVoid extends Module {
    private SliderSetting mode;
    private SliderSetting minFall;

    private final String[] modes = new String[]{"Blink"};

    private BlockPos pos;

    private LinkedList<Packet> packets = new LinkedList<>();

    private boolean blinking, receivedLagback;

    public AntiVoid() {
        super("AntiVoid", category.player, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFall = new SliderSetting("Min Fall", 5.0, 1.5, 8.0, 0.5));
    }
	
	@Override
	public void onDisable() {
		clearPackets();
		blinking = false;
		receivedLagback = false;
		pos = null;
	}

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (mc.thePlayer == null)
            return;

        if(safe()) {
            pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

            receivedLagback = false;

            if(blinking) {
                clearPackets();
                blinking = false;
            }
        } else if(!receivedLagback) {
            if (sb()) {
                if (blinking) {
                    mc.thePlayer.setPosition(pos.getX(), pos.getY(), pos.getZ());
                }
            } else {
                if (!blinking) {
                    blinking = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (!blinking)
            return;

        packets.add(e.getPacket());
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();

            if(blinking) {
                mc.thePlayer.onGround = false;

                clearPackets();

                pos = new BlockPos(s08.getX(), s08.getY(), s08.getZ());

                blinking = false;

                receivedLagback = true;
            }
        }
    }

    private void clearPackets() {
        synchronized (packets) {
            if (!packets.isEmpty()) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
            }
        }
    }

    private boolean isBlockUnder() {
        for(int y = (int) mc.thePlayer.posY; y >= 0; y--) {
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }

    private boolean safe() {
        return isBlockUnder() || mc.thePlayer.ticksExisted < 100;
    }

    private boolean sb() {
        return mc.thePlayer.fallDistance >= minFall.getInput() && !isBlockUnder() && mc.thePlayer.ticksExisted >= 100;
    }
}
