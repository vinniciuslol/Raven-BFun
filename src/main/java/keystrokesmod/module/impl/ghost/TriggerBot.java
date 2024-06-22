package keystrokesmod.module.impl.ghost;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class TriggerBot extends Module {
    // trigger bot vars
    private SliderSetting minCps;
    private SliderSetting maxCps;
    private ButtonSetting weaponOnly;

    // left clicker vars
    private Random rand = new Random();
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;

    public TriggerBot() {
        super("TriggerBot", category.ghost, 0);

        this.registerSetting(minCps = new SliderSetting("Min CPS", 10.0, 1.0, 25.0, 0.5));
        this.registerSetting(maxCps = new SliderSetting("Max CPS", 15.0, 1.0, 25.0, 0.5));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon Only", false));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (mc.thePlayer == null)
            return;

        if (this.j > 0L && this.i > 0L) {
            if (!Utils.holdingWeapon() && weaponOnly.isToggled())
                return;

            if (System.currentTimeMillis() > this.i)
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                    Reflection.clickMouse();
        } else {
            this.gd();
        }
    }

    public void gd() {
        double c = Utils.getRandomValue(minCps, maxCps, this.rand) + 0.4D * this.rand.nextDouble();
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
}
