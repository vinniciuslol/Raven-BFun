package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.utility.Reflection;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Reflection;
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

    private PlayerInfo pos;

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
    public void onTick(TickEvent e) throws IllegalAccessException {
        if (mc.thePlayer == null)
            return;

        if(safe()) {
            pos = new PlayerInfo(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround, mc.thePlayer.fallDistance, mc.thePlayer.inventory.currentItem);

            receivedLagback = false;

            if(blinking) {
                clearPackets();
                blinking = false;
            }
        } else if(!receivedLagback) {
            if(sb()) {
				if(blinking) {
                    mc.thePlayer.setPosition(pos.x, pos.y, pos.z);

                    mc.thePlayer.motionX = pos.motionX;
                    mc.thePlayer.motionZ = pos.motionZ;
                }

                mc.thePlayer.motionY = pos.motionY;

                mc.thePlayer.rotationYaw = pos.yaw;
                mc.thePlayer.rotationPitch = pos.pitch;

                mc.thePlayer.onGround = pos.onGround;

                mc.thePlayer.fallDistance = pos.fallDist;

                mc.thePlayer.inventory.currentItem = pos.itemSlot;
				
				Reflection.currentPlayerItem.set(mc.playerController, pos.itemSlot);
				
                clearPackets();
                
			}
        } else {
            if(!blinking) {
				blinking = true;
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
    public void onReceivePacket(ReceivePacketEvent e) throws IllegalAccessException {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();

            if(blinking) {
                mc.thePlayer.onGround = false;

                mc.thePlayer.fallDistance = pos.fallDist;

                mc.thePlayer.inventory.currentItem = pos.itemSlot;
                Reflection.currentPlayerItem.set(mc.playerController, pos.itemSlot);
				
				clearPackets();

                pos = new PlayerInfo(s08.getX(), s08.getY(), s08.getZ(), 0, 0, 0, s08.getYaw(), s08.getPitch(), false, mc.thePlayer.fallDistance, mc.thePlayer.inventory.currentItem);

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
	
	class PlayerInfo {
        public double x, y, z;
        public double motionX, motionY, motionZ;
        public float yaw, pitch;
        public boolean onGround;
        public float fallDist;
        public int itemSlot;

        public PlayerInfo(double x, double y, double z, double motionX, double motionY, double motionZ, float yaw, float pitch, boolean onGround, float fallDist, int itemSlot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.yaw = yaw;
            this.pitch = pitch;
            this.onGround = onGround;
            this.fallDist = fallDist;
            this.itemSlot = itemSlot;
        }
    }
}
