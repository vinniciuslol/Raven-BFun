package keystrokesmod.module.impl.ghost;

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
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class RightClicker extends Module {
    public SliderSetting minCPS;
    public SliderSetting maxCPS;
    public SliderSetting jitter;
    public ButtonSetting blocksOnly;
    public ButtonSetting noCpsCap;
    private Random rand = null;
    private Method gs;
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;

    public RightClicker() {
        super("Right Clicker", category.ghost, 0);
        this.registerSetting(minCPS = new SliderSetting("Min CPS", 9.0, 1.0, 20.0, 0.5));
        this.registerSetting(maxCPS = new SliderSetting("Max CPS", 12.0, 1.0, 20.0, 0.5));
        this.registerSetting(jitter = new SliderSetting("Jitter", 0.0, 0.0, 3.0, 0.1));
        this.registerSetting(blocksOnly = new ButtonSetting("Inventory fill", false));
        this.registerSetting(noCpsCap = new ButtonSetting("No CPS Cap", false));

        try {
            this.gs = GuiScreen.class.getDeclaredMethod("func_73864_a", Integer.TYPE, Integer.TYPE, Integer.TYPE);
        } catch (Exception var4) {
            try {
                this.gs = GuiScreen.class.getDeclaredMethod("mouseClicked", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            } catch (Exception ignored) {
            }
        }

        if (this.gs != null) {
            this.gs.setAccessible(true);
        }

    }

    public void onEnable() {
        if (this.gs == null) {
            this.disable();
        }

        this.rand = new Random();
    }

    public void onDisable() {
        this.i = 0L;
        this.j = 0L;
    }

    public void guiUpdate() {
        Utils.correctValue(minCPS, maxCPS);
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent ev) throws InvocationTargetException, IllegalAccessException {
        if (ev.phase != Phase.END && Utils.nullCheck() && !mc.thePlayer.isEating()) {
            if (mc.currentScreen == null && mc.inGameHasFocus) {
                if (!blocksOnly.isToggled() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
                    return;

                if (Mouse.isButtonDown(1)) {
                    this.dc();
                } else {
                    this.i = 0L;
                    this.j = 0L;
                }
            }
        }
    }

    public void dc() throws IllegalAccessException {
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

        if (this.j > 0L && this.i > 0L) {
            if (System.currentTimeMillis() > this.i) {
                if (noCpsCap.isToggled())
                    Reflection.rightClickDelayTimerField.set(mc, 0);

                Reflection.rightClick();
            }
        } else {
            this.gd();
        }

    }

    public void gd() {
        double c = Utils.getRandomValue(minCPS, maxCPS, this.rand) + 0.4D * this.rand.nextDouble();
        long d = (int) Math.round(1000.0D / c);
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.n = false;
            }

            this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.n) {
            d = (long) ((double) d * this.m);
        }

        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + (long) this.rand.nextInt(100);
            }

            this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
    }

    private void inventoryClick(GuiScreen s) {
        int x = Mouse.getX() * s.width / mc.displayWidth;
        int y = s.height - Mouse.getY() * s.height / mc.displayHeight - 1;

        try {
            this.gs.invoke(s, x, y, 0);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }

    }
}
