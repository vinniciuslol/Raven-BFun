package keystrokesmod.module.impl.ghost;

import keystrokesmod.Raven;
import keystrokesmod.event.JumpEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.StrafeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AimAssist extends Module {
    private SliderSetting speed;
    private SliderSetting fov;
    private SliderSetting distance;
    private ButtonSetting clickAim;
    private ButtonSetting weaponOnly;
    private ButtonSetting aimInvis;
    private ButtonSetting blatantMode;
    private ButtonSetting ignoreTeammates;
    private ButtonSetting silentAim;
    private float yaw = 0.0F;

    public AimAssist() {
        super("AimAssist", category.ghost, 0);
        this.registerSetting(speed = new SliderSetting("Speed", 45.0D, 1.0D, 100.0D, 1.0D));
        this.registerSetting(fov = new SliderSetting("FOV", 90.0D, 15.0D, 180.0D, 1.0D));
        this.registerSetting(distance = new SliderSetting("Distance", 4.5D, 1.0D, 10.0D, 0.5D));
        this.registerSetting(clickAim = new ButtonSetting("Click aim", true));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(aimInvis = new ButtonSetting("Aim invis", false));
        this.registerSetting(blatantMode = new ButtonSetting("Blatant mode", false));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", false));
        this.registerSetting(silentAim = new ButtonSetting("Silent Aim", false));
    }

    public void onUpdate() {
        if (mc.currentScreen == null && mc.inGameHasFocus) {
            if (!weaponOnly.isToggled() || Utils.holdingWeapon()) {
                if (clickAim.isToggled() && !Mouse.isButtonDown(0)) {
                    return;
                }

                Entity en = this.getEnemy();
                if (en != null) {
                    if (Raven.debugger) {
                        Utils.sendMessage(this.getName() + " &e" + en.getName());
                    }
                    if (!silentAim.isToggled()) {
                        if (blatantMode.isToggled()) {
                            Utils.aim(en, 0.0F);
                        } else {
                            double n = Utils.n(en);
                            if (n > 1.0D || n < -1.0D) {
                                float val = (float) (-(n / (101.0D - (speed.getInput()))));
                                mc.thePlayer.rotationYaw += val;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (silentAim.isToggled()) {
            Entity en = this.getEnemy();
            if (en != null) {
                if (blatantMode.isToggled()) {
                    float[] rots = Utils.gr(en);
                    e.setYaw(rots[0]);
                    e.setPitch(rots[1]);
                } else {
                    double n = Utils.n(en);
                    if (n > 1.0D || n < -1.0D) {
                        float val = (float) (-(n / (101.0D - (speed.getInput()))));
                        e.setYaw(e.getYaw() + val);
                        yaw = e.getYaw() + val;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onJump(JumpEvent e) {
        if (silentAim.isToggled() && getEnemy() != null)
            e.setYaw(yaw);
    }

    @SubscribeEvent
    public void onStrafe(StrafeEvent e) {
        if (silentAim.isToggled() && getEnemy() != null)
            e.setYaw(yaw);
    }

    private Entity getEnemy() {
        final int n = (int)fov.getInput();
        for (final EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {
                if (Utils.isFriended(entityPlayer)) {
                    continue;
                }
                if (ignoreTeammates.isToggled() && Utils.isTeamMate(entityPlayer)) {
                    continue;
                }
                if (!aimInvis.isToggled() && entityPlayer.isInvisible()) {
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(entityPlayer) > distance.getInput()) {
                    continue;
                }
                if (AntiBot.isBot(entityPlayer)) {
                    continue;
                }
                if (!blatantMode.isToggled() && n != 360 && !Utils.inFov((float)n, entityPlayer)) {
                    continue;
                }
                return entityPlayer;
            }
        }
        return null;
    }
}
