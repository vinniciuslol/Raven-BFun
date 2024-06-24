package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Spider extends Module {
    private SliderSetting mode;
	private SliderSetting ticks;

    private final String[] modes = new String[]{"Vanilla", "Jump"};

    public Spider() {
        super("Spider", category.movement, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 1));
		this.registerSetting(ticks = new SliderSetting("Ticks", 7.0, 1.0, 20.0, 1.0));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (mc.thePlayer.isCollidedHorizontally) {
            switch ((int) mode.getInput()) {
                case 0:
                    mc.thePlayer.motionY += 0.40;
                    break;
                case 1:
                    if (mc.thePlayer.ticksExisted % (int) ticks.getInput() == 0) {
                        mc.thePlayer.jump();
                    }
            }
        }
    }
}
