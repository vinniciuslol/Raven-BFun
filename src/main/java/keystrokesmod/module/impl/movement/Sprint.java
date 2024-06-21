package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", category.movement, 0);
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        mc.thePlayer.setSprinting(true);
    }
}
