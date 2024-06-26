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

    private final String[] modes = new String[]{"Packet", "MushPacket", "NoGround"};

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
		
        if (mode.getInput() == 0)
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.05E-6F, mc.thePlayer.posZ, false));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
		if (mode.getInput() == 2) {
			e.setOnGround(false);
			return;
		}
		
		if (target != null) {
			if (mode.getInput() == 1) {
				double offset = Utils.randomizeDouble(25.999E-6F, 30.999E-6F);
				
				if (target.hurtTime >= 6 && mc.thePlayer.ticksExisted % 2 == 0) {
					e.setPosY(e.getPosY() + offset);
					e.setOnGround(false);
				} else
					return;
			}
			
			target = null;
		}
    }
}
