package me.imtoggle.aichat

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand

@Command(value = "aichat")
class ModCommand {

    @Main
    fun toggle() {
        chatting = !chatting
        initChat()
        if (chatting) sendMessage("hello")
        UChat.chat("Ai chat is now ${if (chatting) "on" else "off"}.")
    }

    @SubCommand
    fun reset() {
        resetAll()
    }

    @SubCommand
    fun list() {
        UChat.chat(AIChatMod.chatHistory)
    }

}