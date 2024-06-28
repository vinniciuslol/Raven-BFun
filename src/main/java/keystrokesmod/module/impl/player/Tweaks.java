package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Tweaks extends Module {
    public ButtonSetting removeHitDelay, removeBlockhitDelay, removeJumpTicks, optimizeBlockhit;

    public Tweaks() {
        super("Tweaks", category.player, 0);
        this.registerSetting(removeHitDelay = new ButtonSetting("Remove Hit Delay", true));
        this.registerSetting(removeBlockhitDelay = new ButtonSetting("Remove Blockhit Delay", false));
        this.registerSetting(removeJumpTicks = new ButtonSetting("Remove jump ticks", false));
        this.registerSetting(optimizeBlockhit = new ButtonSetting("Optimize Blockhit", true));
    }

    @SubscribeEvent
    public void onTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !mc.inGameHasFocus || !Utils.nullCheck()) {
            return;
        }
        if (removeHitDelay.isToggled()) {
            try {
                Reflection.leftClickCounter.set(mc, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
        }

        if (removeBlockhitDelay.isToggled()) {
            try {
                Reflection.blockHitDelay.set(mc.playerController, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
        }

        if (removeJumpTicks.isToggled()) {
            try {
                Reflection.jumpTicks.set(mc.thePlayer, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
        }
    }
}
