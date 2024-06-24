package keystrokesmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PreStepEvent extends Event {
    private float stepHeight;

    public PreStepEvent(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }
}
