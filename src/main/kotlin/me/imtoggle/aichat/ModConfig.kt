package me.imtoggle.aichat

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.runAsync

object ModConfig : Config(Mod(AIChatMod.NAME, ModType.UTIL_QOL), "${AIChatMod.MODID}.json") {

    @Dropdown(
        name = "Model",
        options = ["Llama 3.1 70B (Preview)", "Llama 3.1 8B (Preview)", "Llama 3 Groq 70B Tool Use (Preview)", "Llama 3 Groq 8B Tool Use (Preview)", "Meta Llama 3 70B", "Meta Llama 3 8B", "Mixtral 8x7B", "Gemma 7B", "Gemma 2 9B"],
        subcategory = "Main"
    )
    var modelType = 4

    @Switch(
        name = "Public Chat",
        subcategory = "Main"
    )
    var publicChat = false

    @Slider(
        name = "Temperature",
        min = 0f, max = 2f,
        subcategory = "Main"
    )
    var temperature = 1f
        get() = field.coerceIn(0f..2f)

    @Text(
        name = "Message Style",
        description = "Customize message style. Example: <%name%> %message%",
        subcategory = "Main"
    )
    var style = "<%name%> %message%"

    @Button(
        name = "Style",
        text = "Reset",
        subcategory = "Main"
    )
    var resetStyle = Runnable {
        style = "<%name%> %message%"
    }

    @Text(
        name = "API Key",
        description = "Paste your Groq API key here.",
        secure = true,
        subcategory = "Main"
    )
    var apiKey = ""

    @Text(
        name = "Display Name",
        size = 2,
        subcategory = "Bot Customization"
    )
    var displayName = "AI"

    @Text(
        name = "Skin URL",
        size = 2,
        subcategory = "Bot Customization"
    )
    var skinURL = ""

    @Switch(
        name = "Show In Tab",
        subcategory = "Bot Customization"
    )
    var showInTab = false

    @Button(
        name = "",
        text = "Update Skin",
        subcategory = "Bot Customization"
    )
    var updateSkin = Runnable {
        runAsync {
            updateSkin()
        }
    }

    @Dropdown(
        name = "Game Type",
        options = ["Survival", "Creative", "Adventure", "Spectator"],
        subcategory = "Bot Customization"
    )
    var gameType = 3

    @Button(
        name = "Prompt",
        text = "Browse",
        subcategory = "Bot Customization"
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
        addListener("showInTab") {
            if (showInTab) {
                addInfo()
            } else {
                removeInfo()
            }
        }
        addListener("displayName") {
            addInfo()
        }
    }

}