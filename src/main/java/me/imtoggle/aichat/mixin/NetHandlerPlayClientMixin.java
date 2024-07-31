package me.imtoggle.aichat.mixin;

import me.imtoggle.aichat.UtilKt;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {

    @Inject(method = "handleJoinGame", at = @At("TAIL"))
    private void add(S01PacketJoinGame packetIn, CallbackInfo ci) {
        UtilKt.addInfo();
    }
}
