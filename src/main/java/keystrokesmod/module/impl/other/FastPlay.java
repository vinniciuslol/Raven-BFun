package keystrokesmod.module.impl.other;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastPlay extends Module {
    private ButtonSetting bedwarssolo;
    private ButtonSetting bedwarsdupla;
    private ButtonSetting bedwarstrio;
    private ButtonSetting bedwarsquarteto;
    int modulesenabled = 0;

    public FastPlay() {
        super("FastPlay", category.other);
        this.registerSetting(bedwarssolo = new ButtonSetting("Bedwars Solo", true));
        this.registerSetting(bedwarsdupla = new ButtonSetting("Bedwars Dupla", false));
        this.registerSetting(bedwarstrio = new ButtonSetting("Bedwars Trio", false));
        this.registerSetting(bedwarsquarteto = new ButtonSetting("Bedwars Quarteto", false));
    }


    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {

        if (bedwarssolo.isToggled()) {
            sendChatMessage("/play bedwars_solo");
            this.disable();
            return;
        }


        if (bedwarsdupla.isToggled()) {
            sendChatMessage("/play bedwars_duplas");
            this.disable();
            return;
        }


        if (bedwarstrio.isToggled()) {
            sendChatMessage("/play bedwars_trios");
            this.disable();
            return;
        }


        if (bedwarsquarteto.isToggled()) {
            sendChatMessage("/play bedwars_quarteto");
            this.disable();
            return;
        }

    }

    private void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }

    private void sendPrivateMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§u[RAVEN BFUN]§r " + message));
        this.disable();
    }
}