package me.imtoggle.aichat

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.runAsync

object ModConfig : Config(Mod(AIChatMod.NAME, ModType.UTIL_QOL), "${AIChatMod.MODID}.json") {

    @Dropdown(
        name = "Model",
        options = ["Llama 3.1 70B (Preview)", "Llama 3.1 8B (Preview)", "Llama 3 Groq 70B Tool Use (Preview)", "Llama 3 Groq 8B Tool Use (Preview)", "Meta Llama 3 70B", "Meta Llama 3 8B", "Mixtral 8x7B", "Gemma 7B", "Gemma 2 9B"]
    )
    var modelType = 4

    @Slider(
        name = "Temperature",
        min = 0f, max = 2f
    )
    var temperature = 1f
        get() = field.coerceIn(0f..2f)

    @Text(
        name = "Message Style",
        description = "Customize message style. Example: <%name%> %message%"
    )
    var style = "<%name%> %message%"

    @Button(
        name = "Style",
        text = "Reset"
    )
    var resetStyle = Runnable {
        style = "<%name%> %message%"
    }

    @Text(
        name = "API Key",
        description = "Paste your Groq API key here.",
        secure = true
    )
    var apiKey = ""

    @Text(
        name = "Display Name",
        size = 2
    )
    var displayName = "AI"

    @Text(
        name = "Skin URL",
        size = 2
    )
    var skinURL = ""

    @Button(
        name = "",
        text = "Update Skin",
        size = 2
    )
    var updateSkin = Runnable {
        runAsync {
            updateSkin()
        }
    }

    @Dropdown(
        name = "Game Type",
        options = ["Survival", "Creative", "Adventure", "Spectator"]
    )
    var gameType = 3

    @Button(
        name = "Prompt",
        text = "Browse"
    )
    var browse = Runnable {
        runAsync {
            browsePrompt()
        }
    }

    var system: MessageInfo? = null

    init {
        initialize()
        initChat()
        updateSkin()
        addListener("displayName") {
            addInfo()
        }
    }

}