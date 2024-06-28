package keystrokesmod.module.impl.other;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastUse extends Module {
    private SliderSetting ticks;
    private Minecraft mc;

    public FastUse() {
        super("FastUse", category.other);
        this.registerSetting(ticks = new SliderSetting("Ticks", 20, 1, 30, 0.1));
        mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        if (this.isEnabled() && mc.thePlayer != null) {
            EntityPlayer player = mc.thePlayer;
            ItemStack currentItem = player.getHeldItem();

            if (currentItem != null && isEdibleItem(currentItem) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                instantEat(player, currentItem);
            }
        }
    }

    private boolean isEdibleItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.golden_apple ||
                itemStack.getItem() == Items.bread ||
                itemStack.getItem() == Items.cooked_beef;
    }

    private void instantEat(EntityPlayer player, ItemStack itemStack) {
        int currentItemSlot = player.inventory.currentItem;

        // Simulate the eating action by sending packets
        for (int i = 0; i < ((int)ticks.getInput()); i++) { // 20 ticks (1 second) to simulate eating
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
        }

        // Send packet to finish eating
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(itemStack));

        // Simulate the client finishing the use of the item
        player.stopUsingItem();
    }
}
