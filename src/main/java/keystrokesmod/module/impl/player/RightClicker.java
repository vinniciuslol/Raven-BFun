package keystrokesmod.module.impl.player;

import keystrokesmod.utility.pasted.*;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RightClicker extends Module {
    public SliderSetting minCPS;
    public SliderSetting maxCPS;
    public SliderSetting jitter;
    public ButtonSetting blocksOnly;
    public ButtonSetting noCpsCap;
    public ButtonSetting disableOnInventory;

    private Random rand = new Random();
    private TimerUtils timer = new TimerUtils();
    private boolean allow;

    public RightClicker() {
        super("Right Clicker", category.ghost, 0);
        this.registerSetting(minCPS = new SliderSetting("Min CPS", 9.0, 1.0, 20.0, 0.5));
        this.registerSetting(maxCPS = new SliderSetting("Max CPS", 12.0, 1.0, 20.0, 0.5));
        this.registerSetting(jitter = new SliderSetting("Jitter", 0.0, 0.0, 3.0, 0.1));
        this.registerSetting(blocksOnly = new ButtonSetting("Inventory fill", false));
        this.registerSetting(noCpsCap = new ButtonSetting("No CPS Cap", false));
        this.registerSetting(disableOnInventory = new ButtonSetting("Disable On Inventory", true));
    }

    public void guiUpdate() {
        Utils.correctValue(minCPS, maxCPS);
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent e) {
        if (mc.thePlayer == null)
            return;

        if (disableOnInventory.isToggled() && mc.currentScreen != null)
            return;

        if (blocksOnly.isToggled() && mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))
            return;

        long min = (long) (1000 / minCPS.getInput());
        long max = (long) (1000 / maxCPS.getInput());

        long delay = max > min ? ThreadLocalRandom.current().nextLong(max, min) : min;

        if (timer.hasTimeElapsed(delay, true))
            allow = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent e) throws IllegalAccessException {
        if (mc.thePlayer == null)
            return;

        if (disableOnInventory.isToggled() && mc.currentScreen != null)
            return;

        if (Mouse.isButtonDown(1)) {
            if (jitter.getInput() > 0.0D) {
                double a = jitter.getInput() * 0.45D;
                EntityPlayerSP var10000;
                if (this.rand.nextBoolean()) {
                    var10000 = mc.thePlayer;
                    var10000.rotationYaw = (float) ((double) var10000.rotationYaw + (double) this.rand.nextFloat() * a);
                } else {
                    var10000 = mc.thePlayer;
                    var10000.rotationYaw = (float) ((double) var10000.rotationYaw - (double) this.rand.nextFloat() * a);
                }

                if (this.rand.nextBoolean()) {
                    var10000 = mc.thePlayer;
                    var10000.rotationPitch = (float) ((double) var10000.rotationPitch + (double) this.rand.nextFloat() * a * 0.45D);
                } else {
                    var10000 = mc.thePlayer;
                    var10000.rotationPitch = (float) ((double) var10000.rotationPitch - (double) this.rand.nextFloat() * a * 0.45D);
                }
            }
			
			if (allow) {
				if (noCpsCap.isToggled())
					Reflection.rightClickDelayTimerField.set(mc, 0);

				Reflection.rightClick();

				allow = false;
			}
        }
    }
}
