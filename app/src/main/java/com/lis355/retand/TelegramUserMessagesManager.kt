package com.lis355.retand

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class SendMessageResponse

interface TelegramAPI {
    @POST("sendMessage")
    fun sendMessage(@Body body: MutableMap<String, Any>): Call<SendMessageResponse?>?
}

interface TelegramUserMessagesSendable {
    fun sendMessageToUser(text: String)
}

class TelegramUserMessagesManager(private val settings: Settings) : TelegramUserMessagesSendable {
    override fun sendMessageToUser(text: String) {
        if (settings.telegramBotApiToken.isEmpty() ||
            settings.telegramUserId.isEmpty()
        ) return

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot${settings.telegramBotApiToken}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val telegramApi: TelegramAPI = retrofit.create(TelegramAPI::class.java)
        val sendMessageBodyMap: MutableMap<String, Any> = HashMap()
        sendMessageBodyMap["chat_id"] = settings.telegramUserId
        sendMessageBodyMap["text"] = text
        sendMessageBodyMap["parse_mode"] = "HTML"

        telegramApi.sendMessage(sendMessageBodyMap)?.execute()
    }
}