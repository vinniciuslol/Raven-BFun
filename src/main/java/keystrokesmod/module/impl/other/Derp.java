package keystrokesmod.module.impl.other;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Derp extends Module {
    public static DescriptionSetting a;
    private ButtonSetting verticalderp;
    public static SliderSetting speed;
    private float headYawOffset = 0;

    public Derp() {
        super("Derp", category.other);
        this.registerSetting(speed = new SliderSetting("Speed", 50, 1.0, 150.0, 0.1));
        this.registerSetting(verticalderp = new ButtonSetting("Vertical Derp", true));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        headYawOffset += speed.getInput();


        if (headYawOffset >= 360) {
            headYawOffset -= 360;
        }

        if (verticalderp.isToggled()){
            e.setPitch(mc.thePlayer.rotationPitch + headYawOffset);
        }
        mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + headYawOffset;
        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + headYawOffset;
    }
}
