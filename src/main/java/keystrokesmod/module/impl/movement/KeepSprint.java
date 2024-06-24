package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.Switchaura;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class KeepSprint extends Module {
    public static SliderSetting slow;
    public static ButtonSetting disableWhileJump;
    public static ButtonSetting reduceReachHits;

    public KeepSprint() {
        super("KeepSprint", category.movement, 0);
        this.registerSetting(slow = new SliderSetting("Slow %", 40.0D, 0.0D, 40.0D, 1.0D));
        this.registerSetting(disableWhileJump = new ButtonSetting("Disable while jumping", false));
        this.registerSetting(reduceReachHits = new ButtonSetting("Only reduce reach hits", false));
    }

    public static void keepSprint(Entity en) {
        boolean vanilla = false;
        if (disableWhileJump.isToggled() && !mc.thePlayer.onGround) {
            vanilla = true;
        }
        else if (reduceReachHits.isToggled() && !mc.thePlayer.capabilities.isCreativeMode) {
            double n = -1.0;
            final Vec3 getPositionEyes = mc.thePlayer.getPositionEyes(1.0f);
            if (en != null) {
                n = getPositionEyes.distanceTo(Switchaura.target != null ? Switchaura.target.getPositionEyes(1.0f) : en.getPositionEyes(1.0f));
            }
            if (n != -1.0 && n <= 3.0) {
                vanilla = true;
            }
        }
        if (vanilla) {
            mc.thePlayer.motionX *= 0.6;
            mc.thePlayer.motionZ *= 0.6;
        } else {
            float n2 = (100.0f - (float) slow.getInput()) / 100.0f;
            mc.thePlayer.motionX *= n2;
            mc.thePlayer.motionZ *= n2;
        }
    }
}
