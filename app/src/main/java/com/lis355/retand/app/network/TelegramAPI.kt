package com.lis355.retand.app.network

import retrofit2.Call
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