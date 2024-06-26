package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Sprint extends Module {

    public static SliderSetting mode;
    private String[] modes = new String[]{"Sprint", "Omni-Sprint", "Grim"};

    public Sprint() {
        super("Sprint", category.movement, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        switch ((int) mode.getInput())
        {
            case 0:
                mc.thePlayer.setSprinting(true);
                break;
            case 1:
                break;
        }

    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        switch ((int) mode.getInput())
        {
            case 1:
                if(ModuleManager.killAura.target != null){
                    mc.thePlayer.setSprinting(true);
                }
                else{
                    if (ModuleManager.scaffold.isEnabled()){
                        mc.thePlayer.setSprinting(true);
                    }
                    else{
                        if (mc.gameSettings.keyBindForward.isKeyDown()) {
                            mc.thePlayer.setSprinting(true);
                        }
                        if (mc.gameSettings.keyBindBack.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw - 180);
                            mc.thePlayer.setSprinting(true);
                        }
                        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw - 90);
                            mc.thePlayer.setSprinting(true);
                        }

                        if (mc.gameSettings.keyBindRight.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw + 90);
                            mc.thePlayer.setSprinting(true);
                        }
                        if (mc.gameSettings.keyBindRight.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw + 45);
                            mc.thePlayer.setSprinting(true);
                        }
                        if (mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw - 45);
                            mc.thePlayer.setSprinting(true);

                        }
                        if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw - 135);
                            mc.thePlayer.setSprinting(true);
                        }

                        if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw + 135);
                            mc.thePlayer.setSprinting(true);
                        }
                    }
                }

                break;
            case 2:
                if(ModuleManager.killAura.target != null){
                    mc.thePlayer.setSprinting(true);
                }
                else{
                    if (ModuleManager.scaffold.isEnabled()){
                        mc.thePlayer.setSprinting(true);
                    }
                    else{
                        if (mc.gameSettings.keyBindForward.isKeyDown()) {
                            mc.thePlayer.setSprinting(true);
                        }
                        if (mc.gameSettings.keyBindBack.isKeyDown()) {
                            e.setYaw(mc.thePlayer.rotationYaw - 180);
                            mc.thePlayer.setSprinting(true);
                        }
                    }
                }

                break;
        }

    }





}
