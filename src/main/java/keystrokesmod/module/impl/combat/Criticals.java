package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
    private SliderSetting mode;
    private ButtonSetting onlyGround;
	
	private EntityLivingBase target;

    private final String[] modes = new String[]{"Packet", "Mush", "NoGround", "Packet2"};

    public Criticals() {
        super("Criticals", category.combat, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 2));
		this.registerSetting(onlyGround = new ButtonSetting("Only Setting", false));
    }
	
	@Override
	public void onDisable() {
		target = null;
	}

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent e) {
        if (onlyGround.isToggled() && !mc.thePlayer.onGround)
            return;
		
	    if (!(e.target instanceof EntityPlayer))
		    return;
	
	    mc.thePlayer.onCriticalHit(e.target);
		
		target = (EntityLivingBase) e.target;
		
		switch ((int) mode.getInput()) {
			case 0:
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.05E-6F, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
				break;
			case 1:
				double[] offset = new double[]{30.9999E-6F, 40.9999E-7F, 50.9999E-8F, 0, 0};
				
				if (((EntityLivingBase) e.target).hurtTime >= 6 && mc.thePlayer.ticksExisted % 11 == 0)
					for (double o : offset)
						mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + o, mc.thePlayer.posZ, false));
				break;
			case 3:
				if (((EntityLivingBase) e.target).hurtTime >= 3) {
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.600, mc.thePlayer.posZ, false));
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
				}
		}
	}

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
		if (mode.getInput() == 2)
			e.setOnGround(false);
    }
}
