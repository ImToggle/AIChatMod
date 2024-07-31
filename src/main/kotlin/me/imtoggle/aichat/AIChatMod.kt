package me.imtoggle.aichat

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ChatSendEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.renderer.TinyFD
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

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

    var chatting = false

    var chatHistory = ArrayList<MessageInfo>()

    @Subscribe
    fun onSend(event: ChatSendEvent) {
        if (!chatting) return
        if (event.message.startsWith("/")) return
        event.isCancelled = true
        UChat.chat("<${mc.thePlayer.name}> ${event.message}")
        sendMessage(event.message)
    }

    var image: BufferedImage? = null

    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.END) return
        image ?: return
        skinTexture = DynamicTexture(image)
        skinTexture.updateDynamicTexture()
        skinLocation = mc.textureManager.getDynamicTextureLocation("aichatmod", skinTexture)
    }

    fun sendMessage(string: String) {
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

            chatHistory.add(MessageInfo("user", string))

            val body = RequestBody(getModelID(ModConfig.modelType), chatHistory, temperature = ModConfig.temperature)

            OutputStreamWriter(connection.outputStream, "UTF-8").use { os ->
                os.write(Gson().toJson(body))
            }

            BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
                val response = StringBuilder()
                var line: String?
                var a = true
                while (reader.readLine().also { line = it } != null) {
                    if (a) {
                        a = false
                        continue
                    }
                    if (line!!.isEmpty()) continue
                    val str = line!!.substring(6)
                    if (str.length <= 10) continue
                    val message = Gson().fromJson(str, JsonObject::class.java).get("choices").asJsonArray.get(0).asJsonObject.get("delta").asJsonObject
                    if (message.has("content")) {
                        response.append(message.get("content").asString)
                    }

                }

                response.toString().let {
                    UChat.chat("<${ModConfig.displayName}> $it")
                    chatHistory.add(MessageInfo("assistant", it))
                }

            }
        }
    }

    fun browsePrompt() {
        val result = TinyFD.INSTANCE.openFileSelector(
            "Select a txt file",
            "",
            arrayOf("*.txt"),
            "Txt Files"
        ) ?: return
        result.readText().let { text ->
            ModConfig.system?.let { chatHistory.remove(it) }
            ModConfig.system = MessageInfo("system", text)
            chatHistory.add(ModConfig.system!!)
        }
    }

}