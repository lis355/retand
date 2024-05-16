package com.lis355.retand.app.telegram

import com.lis355.retand.app.network.Network
import com.lis355.retand.app.network.TelegramAPI
import com.lis355.retand.app.network.TelegramUserMessagesSendable
import com.lis355.retand.app.settings.Settings

class TelegramUserMessagesManager(private val settings: Settings) : TelegramUserMessagesSendable {
    override fun sendMessageToUser(text: String) {
        if (settings.telegramBotApiToken.isEmpty() ||
            settings.telegramUserId.isEmpty()
        ) return

        val sendMessageBodyMap: MutableMap<String, Any> = HashMap()
        sendMessageBodyMap["chat_id"] = settings.telegramUserId
        sendMessageBodyMap["text"] = text
        sendMessageBodyMap["parse_mode"] = "HTML"

        val telegramApi = Network.createAPI("https://api.telegram.org/bot${settings.telegramBotApiToken}/", TelegramAPI::class.java)
        telegramApi.sendMessage(sendMessageBodyMap)?.execute()
    }
}