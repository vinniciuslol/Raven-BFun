package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PostMotionEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {
    public static SliderSetting mode;
    public static SliderSetting slowed;
    public static ButtonSetting disableBow;
    public static ButtonSetting disablePotions;
    public static ButtonSetting swordOnly;
    public static ButtonSetting vanillaSword;
    private String[] modes = new String[]{"Vanilla", "SwitchItem", "Grim"};
    private boolean postPlace, lastUsingItem;

    public NoSlow() {
        super("NoSlow", Module.category.movement, 0);
        this.registerSetting(new DescriptionSetting("Default is 80% motion reduction."));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(slowed = new SliderSetting("Slow %", 80.0D, 0.0D, 80.0D, 1.0D));
        this.registerSetting(disableBow = new ButtonSetting("Disable bow", false));
        this.registerSetting(disablePotions = new ButtonSetting("Disable potions", false));
        this.registerSetting(swordOnly = new ButtonSetting("Sword only", false));
        this.registerSetting(vanillaSword = new ButtonSetting("Vanilla sword", false));
    }

    public void onUpdate() {
        if (ModuleManager.bedAura.stopAutoblock) {
            return;
        }

        postPlace = false;
        if (vanillaSword.isToggled() && Utils.holdingSword()) {
            return;
        }

        boolean apply = getSlowed() != 0.2f;

        if (!apply || !isUsingItem()) {
            return;
        }

        if (mode.getInput() == 1) {
            if (mc.thePlayer.ticksExisted % 3 == 0 && !Raven.badPacketsHandler.C07) {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0, 0, 0));
            }
        } else if (mode.getInput() == 2) {
            int slot = mc.thePlayer.inventory.currentItem;

            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

            if(lastUsingItem) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        }
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        lastUsingItem = isUsingItem();
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if(e.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) e.getPacket();

            if(packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if(Utils.holdingWeapon() && mode.getInput() == 2) {
                    e.setCanceled(true);

                    int slot = mc.thePlayer.inventory.currentItem;

                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                }
            }
        }
    }

    public static float getSlowed() {
        if (mc.thePlayer.getHeldItem() == null || ModuleManager.noSlow == null || !ModuleManager.noSlow.isEnabled()) {
            return 0.2f;
        } else {
            if (swordOnly.isToggled() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
                return 0.2f;
            }
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && disableBow.isToggled()) {
                return 0.2f;
            } else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getItemDamage()) && disablePotions.isToggled()) {
                return 0.2f;
            }
        }
        float val = (100.0F - (float) slowed.getInput()) / 100.0F;
        return val;
    }

    private boolean isUsingItem() {
        return mc.thePlayer.isUsingItem() || (ModuleManager.killAura.block && ModuleManager.killAura.autoblockMode.getInput() != 0);
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }
}
