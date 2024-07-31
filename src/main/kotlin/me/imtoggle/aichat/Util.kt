package me.imtoggle.aichat

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.renderer.TinyFD
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import cc.polyfrost.oneconfig.utils.dsl.substringTo
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import me.imtoggle.aichat.AIChatMod.chatHistory
import me.imtoggle.aichat.mixin.NetHandlerPlayClientAccessor
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.EnumChatFormatting
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.stream.Collectors
import javax.imageio.ImageIO

var chatting = false
    set(value) {
        field = value
        if (value) {
            addInfo()
        } else {
            removeInfo()
        }
    }

var currentUUID = UUID.randomUUID()

var skinTexture = DynamicTexture(64, 64)

var nextAcceptTime = System.currentTimeMillis()

var skinLocation = mc.textureManager.getDynamicTextureLocation("aichatmod", skinTexture)

enum class ModelTypes(val id: String) {
    LlamaPreview70B("llama-3.1-70b-versatile"),
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
    if (!ModConfig.showInTab) return
    if (!chatting) return
    removeInfo()
    mc.netHandler ?: return
    currentUUID = UUID.randomUUID()
    (mc.netHandler as NetHandlerPlayClientAccessor).playerInfoMap[currentUUID] = DummyInfo(GameProfile(currentUUID, EnumChatFormatting.getTextWithoutFormattingCodes(ModConfig.displayName)))
}

fun removeInfo() {
    mc.netHandler ?: return
    (mc.netHandler as NetHandlerPlayClientAccessor).playerInfoMap.let {
        if (it.contains(currentUUID)) it.remove(currentUUID)
    }
}

val DEFAULT_SKIN = "https://s.namemc.com/i/154ada80149f51f2.png"

fun updateSkin() {
    AIChatMod.image = ImageIO.read(URL(ModConfig.skinURL.let {
        it.ifEmpty { DEFAULT_SKIN }
    })) ?: ImageIO.read(URL(DEFAULT_SKIN))
}

fun initChat() {
    chatHistory.clear()
    ModConfig.system?.let { addMessage(it) }
}

fun resetAll() {
    chatHistory.clear()
    ModConfig.system = null
}

fun setSystemMessage(string: String) {
    ModConfig.system?.let { chatHistory.remove(it) }
    if (string.isEmpty()) return
    ModConfig.system = MessageInfo("system", string)
    addMessage(ModConfig.system!!)
}

fun getMessage(sender: String, message: String): String {
    return ModConfig.style.replace("%name%", sender).replace("%message%", message)
}

fun sendMessage(msg: String) {
    if (nextAcceptTime > System.currentTimeMillis()) return
    nextAcceptTime = System.currentTimeMillis() + 2000
    runAsync {
        val url = URL("https://api.groq.com/openai/v1/chat/completions")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "AiChat/1.0.0")
        connection.setRequestProperty("authorization", "Bearer ${ModConfig.apiKey}")
        connection.doOutput = true
        connection.doInput = true

        addMessage(MessageInfo("user", msg))

        val body = RequestBody(getModelID(ModConfig.modelType), chatHistory, temperature = ModConfig.temperature)

        OutputStreamWriter(connection.outputStream, "UTF-8").use { os ->
            os.write(Gson().toJson(body))
        }

        BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
            val response = StringBuilder()

            val lines = reader.lines().collect(Collectors.toList())
            for ((i, string) in lines.withIndex()) {
                if (i !in 2..lines.size - 6) continue
                if (string.isEmpty()) continue
                val str = string!!.substring(6)
                val message = Gson().fromJson(str, JsonObject::class.java).get("choices").asJsonArray.get(0).asJsonObject.get("delta").asJsonObject
                if (message.has("content")) {
                    response.append(message.get("content").asString)
                }
            }

            ChatAllowedCharacters.filterAllowedCharacters(response.toString()).let {
                addMessage(MessageInfo("assistant", it))
                getMessage(ModConfig.displayName, it).let { text ->
                    if (ModConfig.publicChat) {
                        while (true) {
                            if (System.currentTimeMillis() >= AIChatMod.sendTime) {
                                UChat.say(text.substringTo(99))
                                break
                            }
                        }
                    } else {
                        UChat.chat(text)
                    }
                }
            }

        }
    }
}

fun browsePrompt() {
    val result = TinyFD.INSTANCE.openFileSelector("Select a txt file", "", arrayOf("*.txt"), "Txt Files") ?: return
    setSystemMessage(result.readText())
}

fun addMessage(info: MessageInfo) {
    chatHistory.add(info)
    if (chatHistory.size > 50) {
        var removeIndex = -1
        for ((i, msgInfo) in chatHistory.withIndex()) {
            if (msgInfo.role == "system") continue
            removeIndex = i
            break
        }
        if (removeIndex != -1) chatHistory.removeAt(removeIndex)
    }
}