package me.imtoggle.aichat

data class RequestBody(val model: String, val messages: List<MessageInfo>, val temperature: Float = 1f, val max_tokens: Int = 1024, val top_p: Int = 1, val stop: String? = null, val stream: Boolean = true)