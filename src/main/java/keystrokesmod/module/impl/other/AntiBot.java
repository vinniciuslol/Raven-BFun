package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntiBot extends Module {
    private static final HashMap<EntityPlayer, Long> entities = new HashMap();
    private static ButtonSetting entitySpawnDelay;
    private SliderSetting delay;
    private static ButtonSetting pitSpawn;
    private static ButtonSetting tablist;

    public AntiBot() {
        super("AntiBot", category.other, 0);
        this.registerSetting(entitySpawnDelay = new ButtonSetting("Entity spawn delay", false));
        this.registerSetting(delay = new SliderSetting("Delay", 7.0, 0.5, 15.0, 0.5, " second"));
        this.registerSetting(pitSpawn = new ButtonSetting("Pit spawn", false));
        this.registerSetting(tablist = new ButtonSetting("Tab list", false));
    }

    @SubscribeEvent
    public void c(final EntityJoinWorldEvent entityJoinWorldEvent) {
        if (entitySpawnDelay.isToggled() && entityJoinWorldEvent.entity instanceof EntityPlayer && entityJoinWorldEvent.entity != mc.thePlayer) {
            entities.put((EntityPlayer) entityJoinWorldEvent.entity, System.currentTimeMillis());
        }
    }

    public void onUpdate() {
        if (entitySpawnDelay.isToggled() && !entities.isEmpty()) {
            entities.values().removeIf(n -> n < System.currentTimeMillis() - delay.getInput());
        }
    }

    public void onDisable() {
        entities.clear();
    }

    public static boolean isBot(Entity entity) {
        if (!ModuleManager.antiBot.isEnabled()) {
            return false;
        }

        if (!(entity instanceof EntityPlayer)) {
            return true;
        }
        final EntityPlayer entityPlayer = (EntityPlayer) entity;
        if (entitySpawnDelay.isToggled() && !entities.isEmpty() && entities.containsKey(entityPlayer)) {
            return true;
        }
        if (entityPlayer.isDead) {
            return true;
        }
        if (entityPlayer.getName().isEmpty()) {
            return true;
        }
        if (!getTablist().contains(entityPlayer.getName()) && tablist.isToggled()) {
            return true;
        }
        if (entityPlayer.getHealth() != 20.0f && entityPlayer.getName().startsWith("§c")) {
            return true;
        }
        if (pitSpawn.isToggled() && entityPlayer.posY >= 114 && entityPlayer.posY <= 130 && entityPlayer.getDistance(0, 114, 0) <= 25) {
            if (Utils.isHypixel()) {
                List<String> sidebarLines = Utils.getSidebarLines();
                if (!sidebarLines.isEmpty() && Utils.stripColor(sidebarLines.get(0)).contains("THE HYPIXEL PIT")) {
                    return true;
                }
            }
        }
        if (entityPlayer.maxHurtTime == 0) {
            if (entityPlayer.getHealth() == 20.0f) {
                String unformattedText = entityPlayer.getDisplayName().getUnformattedText();
                if (unformattedText.length() == 10 && unformattedText.charAt(0) != '§') {
                    return true;
                }
                if (unformattedText.length() == 12 && entityPlayer.isPlayerSleeping() && unformattedText.charAt(0) == '§') {
                    return true;
                }
                if (unformattedText.length() >= 7 && unformattedText.charAt(2) == '[' && unformattedText.charAt(3) == 'N' && unformattedText.charAt(6) == ']') {
                    return true;
                }
                if (entityPlayer.getName().contains(" ")) {
                    return true;
                }
            } else if (entityPlayer.isInvisible()) {
                String unformattedText = entityPlayer.getDisplayName().getUnformattedText();
                if (unformattedText.length() >= 3 && unformattedText.charAt(0) == '§' && unformattedText.charAt(1) == 'c') {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> getTablist() {
        List<String> tab = new ArrayList<>();
        for (NetworkPlayerInfo networkPlayerInfo : Utils.getTablist()) {
            if (networkPlayerInfo == null) {
                continue;
            }
            tab.add(networkPlayerInfo.getGameProfile().getName());
        }
        return tab;
    }
}
