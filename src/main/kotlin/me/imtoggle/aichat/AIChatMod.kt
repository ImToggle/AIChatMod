package me.imtoggle.aichat

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ChatSendEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.awt.image.BufferedImage

@Mod(modid = AIChatMod.MODID, name = AIChatMod.NAME, version = AIChatMod.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object AIChatMod {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        CommandManager.INSTANCE.registerCommand(ModCommand())
        EventManager.INSTANCE.register(this)
    }

    var sendTime = System.currentTimeMillis() //some servers have message sending cooldown

    var chatHistory = ArrayList<MessageInfo>()

    var image: BufferedImage? = null

    @Subscribe
    fun onSend(event: ChatSendEvent) {
        if (!chatting) return
        if (ModConfig.publicChat){
            if (event.message.startsWith(".chat ")) {
                sendTime = System.currentTimeMillis() + 2000
            }
            return
        }
        if (event.message.startsWith("/")) return
        event.isCancelled = true
        UChat.chat(getMessage(mc.thePlayer.name, event.message))
        sendMessage(event.message)
    }

    @Subscribe
    fun onReceive(event: ChatEvent) {
        if (!chatting) return
        if (!ModConfig.publicChat) return
        if (!event.text.contains(".chat ")) return
        sendMessage(event.text.replace(".chat ", ""))
    }

    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.END) return
        image ?: return
        skinTexture = DynamicTexture(image)
        image = null
        skinTexture.updateDynamicTexture()
        skinLocation = mc.textureManager.getDynamicTextureLocation("aichatmod", skinTexture)
    }

}