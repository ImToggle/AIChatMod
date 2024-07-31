package me.imtoggle.aichat

import cc.polyfrost.oneconfig.utils.dsl.mc
import com.mojang.authlib.GameProfile
import me.imtoggle.aichat.mixin.NetHandlerPlayClientAccessor
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.EnumChatFormatting
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

var currentUUID = UUID.randomUUID()

var skinTexture = DynamicTexture(64, 64)
var skinLocation = mc.textureManager.getDynamicTextureLocation("aichatmod", skinTexture)

enum class ModelTypes(val id: String) {
    LlamaPreview70B( "llama-3.1-70b-versatile"),
    LlamaPreview8B("llama-3.1-8b-instant"),
    LlamaTool70B("llama3-groq-70b-8192-tool-use-preview"),
    LlamaTool8B("llama3-groq-8b-8192-tool-use-preview"),
    MetaLlama70B("llama3-70b-8192"),
    MetaLlama8B("llama3-8b-8192"),
    Mistral8x7B("mixtral-8x7b-32768"),
    Gemma7B("gemma-7b-it"),
    Gemma9B("gemma2-9b-it")
}

fun getModelID(index: Int): String {
    return ModelTypes.entries[index].id
}

fun addInfo() {
    mc.netHandler ?: return
    (mc.netHandler as NetHandlerPlayClientAccessor).playerInfoMap.let {
        if (it.contains(currentUUID)) it.remove(currentUUID)
        currentUUID = UUID.randomUUID()
        it[currentUUID] = DummyInfo(GameProfile(currentUUID, EnumChatFormatting.getTextWithoutFormattingCodes(ModConfig.displayName)))
    }
}

val DEFAULT_SKIN = "https://s.namemc.com/i/154ada80149f51f2.png"

fun updateSkin() {
    AIChatMod.image = ImageIO.read(URL(ModConfig.skinURL.let { it.ifEmpty { DEFAULT_SKIN } })) ?: ImageIO.read(URL(DEFAULT_SKIN))
}