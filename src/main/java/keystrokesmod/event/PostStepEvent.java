package keystrokesmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PostStepEvent extends Event {
    private float stepHeight;

    public PostStepEvent(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public float getStepHeight() {
        return stepHeight;
    }
}
