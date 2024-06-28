package keystrokesmod.mixins.impl.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
    @Shadow public abstract boolean isChannelOpen();

    @Shadow private Channel channel;

    @Shadow public abstract void sendPacket(Packet packetIn);

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet p_sendPacket_1_, CallbackInfo ci) {
        if (PacketUtils.skipSendEvent.contains(p_sendPacket_1_)) {
            PacketUtils.skipSendEvent.remove(p_sendPacket_1_);
            return;
        }

        SendPacketEvent sendPacketEvent = new SendPacketEvent(p_sendPacket_1_);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(sendPacketEvent);

        if (ModuleManager.tweaks.isEnabled() && ModuleManager.tweaks.optimizeBlockhit.isToggled()) {
            if (sendPacketEvent.getPacket() instanceof C08PacketPlayerBlockPlacement || sendPacketEvent.getPacket() instanceof C07PacketPlayerDigging)
                if (this.isChannelOpen())
                    this.channel.flush();
        }

        if (sendPacketEvent.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_, CallbackInfo ci) {
        if (PacketUtils.skipReceiveEvent.contains(p_channelRead0_2_)) {
            PacketUtils.skipReceiveEvent.remove(p_channelRead0_2_);
            return;
        }
        ReceivePacketEvent receivePacketEvent = new ReceivePacketEvent(p_channelRead0_2_);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(receivePacketEvent);

        if (receivePacketEvent.isCanceled()) {
            ci.cancel();
        }
    }
}
