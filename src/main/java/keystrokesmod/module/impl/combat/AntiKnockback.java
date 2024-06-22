package keystrokesmod.module.impl.combat;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class AntiKnockback extends Module {
    private SliderSetting mode;
    private ButtonSetting cancelExplosion;
    private ButtonSetting lobbyCheck;
    private String[] modes = new String[]{"Cancel", "Hypixel"};

    public AntiKnockback() {
        super("AntiKnockback", category.combat);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion packet", true));
        this.registerSetting(lobbyCheck = new ButtonSetting("Lobby check", false));
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck() || LongJump.stopModules || e.isCanceled()) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                if (lobbyCheck.isToggled() && isLobby()) {
                    return;
                }

                if (ModuleManager.bedAura.cancelKnockback())
                    return;

                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) e.getPacket();

                switch ((int) mode.getInput()) {
                    case 0:
                        e.setCanceled(true);
                        break;
                    case 1:
                        e.setCanceled(true);
                        mc.thePlayer.motionX = ((double) (s12PacketEntityVelocity.getMotionX() / 8000) * 0) / 100;
                        mc.thePlayer.motionZ = ((double) (s12PacketEntityVelocity.getMotionZ() / 8000) * 0) / 100;
                        break;
                }
            }
        }

        if (e.getPacket() instanceof S27PacketExplosion) {
            if (lobbyCheck.isToggled() && isLobby()) {
                return;
            }

            if (ModuleManager.bedAura.cancelKnockback())
                return;

            if (cancelExplosion.isToggled())
                e.setCanceled(true);
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isLobby() {
        if (Utils.isHypixel()) {
            List<String> sidebarLines = Utils.getSidebarLines();
            if (!sidebarLines.isEmpty()) {
                String[] parts = Utils.stripColor(sidebarLines.get(1)).split("  ");
                if (parts.length > 1 && parts[1].charAt(0) == 'L') {
                    return true;
                }
            }
        }
        return false;
    }
}
