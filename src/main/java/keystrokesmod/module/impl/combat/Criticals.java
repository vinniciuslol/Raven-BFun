package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
    private SliderSetting mode;
    private ButtonSetting onlyGround;

    private final String[] modes = new String[]{"Packet", "CrisPacket", "NoGround"};

    public Criticals() {
        super("Criticals", category.combat, 0);
        this.registerSetting(mode);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent e) {
        if (onlyGround.isToggled() && !mc.thePlayer.onGround)
            return;

        switch ((int) mode.getInput()) {
            case 0:
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.05E-6F, mc.thePlayer.posZ, false));
                break;
            case 1:
                double offset = Utils.randomizeDouble(1.010E-2F, 2.001E-2F);

                if (((EntityLivingBase) e.target).hurtTime >= 5)
                    if (mc.thePlayer.ticksExisted % 4 == 0)
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                break;
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (mode.getInput() == 2)
            e.setOnGround(false);
    }
}
