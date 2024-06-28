package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastUse extends Module {
    private SliderSetting ticks;

    public FastUse() {
        super("FastUse", category.other);
        this.registerSetting(ticks = new SliderSetting("Ticks", 20, 1, 30, 0.1));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (mc.thePlayer.getHeldItem() != null && isEdibleItem(mc.thePlayer.getHeldItem()) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            for (int i = 0; i < ((int)ticks.getInput()); i++) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

            mc.thePlayer.stopUsingItem();
        }
    }

    private boolean isEdibleItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.golden_apple ||
                itemStack.getItem() == Items.bread ||
                itemStack.getItem() == Items.cooked_beef;
    }
}
