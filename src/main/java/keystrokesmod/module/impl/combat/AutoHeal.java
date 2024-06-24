package keystrokesmod.module.impl.combat;

import keystrokesmod.event.*;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import keystrokesmod.utility.pasted.TimerUtils;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;

public class AutoHeal extends Module {
	private ButtonSetting soup;
	private ButtonSetting potion;
	private SliderSetting health;
    private SliderSetting maxHealth;
	private SliderSetting delay;
	private ButtonSetting useRegenPot;
    private ButtonSetting predict;
    private ButtonSetting overpot;
    private TimerUtils timer = new TimerUtils();
    public static int haltTicks;
    public static boolean potting;

	public AutoHeal() {
		super("AutoHeal", category.combat, 0);
		this.registerSetting(soup = new ButtonSetting("Soup", true));
		this.registerSetting(potion = new ButtonSetting("Potion", true));
		this.registerSetting(health = new SliderSetting("Health", 15.0, 0.5, 20.0, 0.1));
        this.registerSetting(delay = new SliderSetting("Delay", 50.0, 10.0, 500.0, 5.0));
	    this.registerSetting(new DescriptionSetting("Potion Options"));
        this.registerSetting(maxHealth = new SliderSetting("Max Health before healthing", 5.0, 0.5, 10.0, 0.5));
        this.registerSetting(useRegenPot = new ButtonSetting("Use Regen Pot", false));
        this.registerSetting(predict = new ButtonSetting("Potion Predict", false));
        this.registerSetting(overpot = new ButtonSetting("Overpot", false));
    }
	
	@SubscribeEvent
	public void onPreUpdate(PreUpdateEvent e) {
		int soupSlot = getSoupFromInventory();
		
        if (soup.isToggled() && soupSlot != -1 && mc.thePlayer.getHealth() < health.getInput() && timer.hasTimeElapsed((long) delay.getInput(), true)) {
			this.swap(getSoupFromInventory(), 6);
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(6));
            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
			PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
	}

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (potion.isToggled()) {
            if (potting && haltTicks < 0) {
                potting = false;
            }

            float health = (float) (this.maxHealth.getInput() * 2.0f);

            if (mc.thePlayer.getEquipmentInSlot(4) == null && this.hasArmor(mc.thePlayer) && overpot.isToggled()) {
                health += ((mc.thePlayer.getEquipmentInSlot(1) == null) ? 6.0f : 3.0f);
            }

            long delay = (long) this.delay.getInput();

            if (Utils.isMoving()) {
                if (mc.thePlayer.getHealth() <= health && this.getPotionFromInv() != -1 && timer.hasTimeElapsed(delay, true)) {
                    haltTicks = 6;
                    this.swap(this.getPotionFromInv(), 6);
                    e.setPitch(0.0f);
                    if (predict.isToggled()) {
                        double movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 16.0;
                        double movedPosY = mc.thePlayer.getEntityBoundingBox().minY - 3.6;
                        double movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 16.0;
                        float[] predRot = RotationUtils.getRotationFromPosition(movedPosX, movedPosZ, movedPosY);
                        e.setYaw(predRot[0]);
                        e.setPitch(predRot[1]);
                    }
                    potting = true;
                }
            }
            else if (mc.thePlayer.getHealth() <= health && this.getPotionFromInv() != -1 && this.timer.hasTimeElapsed(delay, true) && haltTicks < 0 && mc.thePlayer.isCollidedVertically) {
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, -90.0f, true));
                this.swap(this.getPotionFromInv(), 6);
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(6));
                PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                haltTicks = 5;
                potting = true;
            }
            --haltTicks;
        }
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        if (potting) {
            if (Utils.isMoving()) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(6));
                PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    }
	
	private void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }
    
    private int getSoupFromInventory() {
        int soup = -1;
        for (int i = 1; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (Item.getIdFromItem(item) == 282) {
                    soup = i;
                }
            }
        }
        return soup;
    }
    private boolean hasArmor(final EntityPlayer player) {
        ItemStack boots = player.inventory.armorInventory[0];
        ItemStack pants = player.inventory.armorInventory[1];
        ItemStack chest = player.inventory.armorInventory[2];
        ItemStack head = player.inventory.armorInventory[3];
        return boots != null || pants != null || chest != null || head != null;
    }

    private int getPotionFromInv() {
        int pot = -1;
        for (int i = 0; i < 45; ++i) {
            if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion)item;
                    if (potion.getEffects(is) != null) {
                        for (Object o : potion.getEffects(is)) {
                            PotionEffect effect = (PotionEffect) o;
                            if ((effect.getPotionID() == Potion.heal.id || effect.getPotionID() == Potion.moveSpeed.id || (effect.getPotionID() == Potion.regeneration.id && useRegenPot.isToggled() && !mc.thePlayer.isPotionActive(Potion.regeneration))) && ItemPotion.isSplash(is.getItemDamage())) {
                                pot = i;
                            }
                        }
                    }
                }
            }
        }
        return pot;
    }
}