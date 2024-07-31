package me.imtoggle.aichat.mixin;

import cc.polyfrost.oneconfig.events.EventManager;
import me.imtoggle.aichat.ChatEvent;
import me.imtoggle.aichat.UtilKt;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
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

    @Inject(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V"))
    private void post(S02PacketChat packetIn, CallbackInfo ci) {
        EventManager.INSTANCE.post(new ChatEvent(EnumChatFormatting.getTextWithoutFormattingCodes(packetIn.getChatComponent().getUnformattedText())));
    }
}
