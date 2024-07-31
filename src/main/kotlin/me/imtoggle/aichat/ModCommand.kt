package me.imtoggle.aichat

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand

@Command(value = "aichat")
class ModCommand {

    @Main
    fun toggle() {
        AIChatMod.chatting = !AIChatMod.chatting
        UChat.chat("Ai chat is now ${if (AIChatMod.chatting) "on" else "off"}.")
    }

    @SubCommand
    fun list() {
        UChat.chat(AIChatMod.chatHistory)
    }

}