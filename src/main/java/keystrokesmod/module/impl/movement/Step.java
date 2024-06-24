package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Step extends Module {
    private SliderSetting mode;
    private SliderSetting stepHeight;
    private final String[] modes = new String[]{"Vanilla"};

    public Step() {
        super("Step", category.movement, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(stepHeight = new SliderSetting("Step Height", 1.0, 0.5, 10.0, 0.5));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if ((int) mode.getInput() == 0) {
            mc.thePlayer.stepHeight = (float) stepHeight.getInput();
        }
    }
}
