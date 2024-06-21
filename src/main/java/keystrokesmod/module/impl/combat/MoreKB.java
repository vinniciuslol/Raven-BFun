package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoreKB extends Module {
    private SliderSetting mode;
    private SliderSetting packets;

    private Entity en;

    private final String[] modes = new String[]{"Packet", "DoublePacket", "PacketSneak", "WTap", "STap", "NoStop"};

    public MoreKB() {
        super("MoreKB", category.combat, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 5));
        this.registerSetting(packets = new SliderSetting("Packets", 1.0, 1.0, 15.0, 1.0));
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent e) {
        if (!(e.target instanceof EntityPlayer))
            return;

        en = e.target;

        for (int i = 0; i < packets.getInput(); i++) {
            switch ((int) mode.getInput()) {
                case 0:
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    break;
                case 1:
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    break;
                case 2:
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
        }
    }

    @SubscribeEvent
    public void onLiving(LivingEvent e) {
        if (en != null) {
            switch ((int) mode.getInput()) {
                case 3:
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.moveForward = 0.0F;
                    mc.thePlayer.setSprinting(true);
                    break;
                case 4:
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.moveForward = -10.0F;
                    mc.thePlayer.setSprinting(true);
                    break;
                case 5:
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.setSprinting(true);
            }
        }
    }
}
