package keystrokesmod.module.impl.ghost;

import keystrokesmod.event.JumpEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JumpReset extends Module {
    private SliderSetting chance;
    private boolean jump;

    public JumpReset() {
        super("Jump Reset", category.ghost);
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1, "%"));
    }

    public void onDisable() {
        jump = false;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent ev) {
        if (Utils.nullCheck()) {
            if (chance.getInput() == 0) {
                return;
            }
            if (mc.thePlayer.maxHurtTime <= 0) {
                jump = false;
                return;
            }
            if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
                jump = true;
            }
            if (!jump || mc.thePlayer.hurtTime == 0) {
                jump = false;
                return;
            }
            if (chance.getInput() != 100.0D) {
                double ch = Math.random();
                if (ch >= chance.getInput() / 100.0D) {
                    return;
                }
            }
            if (jump && mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                jump = false;
            }
        }
    }
}
