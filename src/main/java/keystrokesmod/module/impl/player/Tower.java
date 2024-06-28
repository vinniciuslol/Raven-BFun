package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Tower extends Module {
    private SliderSetting mode;
    private SliderSetting speed;
    private SliderSetting diagonalSpeed;
    private ButtonSetting disableWhileCollided;
    private ButtonSetting disableWhileHurt;
    private ButtonSetting sprintJumpForward;
	int tick, ticks;
    private String[] modes = new String[]{"Vanilla", "LowHop"};
    public Tower() {
        super("Tower", category.player);
        this.registerSetting(new DescriptionSetting("Works with Safewalk & Scaffold"));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 5, 0.5, 9, 0.1));
        this.registerSetting(diagonalSpeed = new SliderSetting("Diagonal speed", 5, 0, 10, 0.1));
        this.registerSetting(disableWhileCollided = new ButtonSetting("Disable while collided", false));
        this.registerSetting(disableWhileHurt = new ButtonSetting("Disable while hurt", false));
        this.registerSetting(sprintJumpForward = new ButtonSetting("Sprint jump forward", false));
        this.canBeEnabled = false;
    }
	
	@Override
	public void onDisable() {
		tick = ticks = 0;
	}

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) throws IllegalAccessException {
        if (canTower()) {
            Utils.setSpeed(Math.max((diagonal() ? diagonalSpeed.getInput() : speed.getInput()) * 0.1 - 0.25, 0));
            if ((int) mode.getInput() == 0) {
                mc.thePlayer.jump();
            } else if (mode.getInput() == 1) {
                if (!ModuleManager.tweaks.isEnabled() || (ModuleManager.tweaks.isEnabled() && !ModuleManager.tweaks.removeJumpTicks.isToggled()))
                    Reflection.jumpTicks.set(mc.thePlayer, 0);

                double amount = Math.random() * 0.000000001;

                int stateY = (int) Math.round((e.getPosY() % 1) * 10000);
                Vec3 mot = new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

                if (!e.isOnGround()) {
                    if (stateY == 4200) {
                        tick = 1;
                    } else if (tick > 0) {
                        tick++;
                    }
                } else {
                    tick = 0;
                }

                if (stateY == 1661) {
                    double diff = 0.04 + Math.random() / 1000d;
                    mc.thePlayer.motionX = mot.xCoord;
                    mc.thePlayer.motionY = mot.yCoord - diff;
                    mc.thePlayer.motionZ = mot.zCoord;
                    ticks = 1;
                } else if (ticks == 1) {
                    mc.thePlayer.motionX = mot.xCoord;
                    mc.thePlayer.motionY = mot.yCoord - 2;
                    mc.thePlayer.motionZ = mot.zCoord;
                    ticks = 2;
                } else if (ticks == 2 && e.getPosY() % 1 == 0) {
                    ticks = 0;
                    e.setPosY(e.getPosY() + amount);
                }
            }
        }
    }

    private boolean canTower() {
        if (!Utils.nullCheck() || !Utils.jumpDown()) {
            return false;
        } else if (disableWhileHurt.isToggled() && mc.thePlayer.hurtTime >= 9) {
            return false;
        } else if (disableWhileCollided.isToggled() && mc.thePlayer.isCollidedHorizontally) {
            return false;
        } else if (modulesEnabled()) {
            return true;
        }

        return false;
    }

    private boolean modulesEnabled() {
        return  ((ModuleManager.safeWalk.isEnabled() && ModuleManager.safeWalk.tower.isToggled() && SafeWalk.canSafeWalk()) || (ModuleManager.scaffold.isEnabled() && ModuleManager.scaffold.tower.isToggled()));
    }

    public boolean canSprint() {
        return canTower() && this.sprintJumpForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) && Utils.jumpDown();
    }

    private boolean diagonal() {
        return (Math.abs(mc.thePlayer.motionX) > 0.05 && Math.abs(mc.thePlayer.motionZ) > 0.05);
    }
}