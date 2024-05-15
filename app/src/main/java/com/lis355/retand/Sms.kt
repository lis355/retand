package com.lis355.retand

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class Sms(val address: String, val text: String)

class SmsBroadcastReceiver(val smsHandler: (Sms) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        for (message in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            if (message.originatingAddress != null &&
                message.messageBody != null
            ) smsHandler(Sms(message.originatingAddress!!, message.messageBody))
        }
    }
}

class SmsProcessor(private val telegramUserMessagesSendable: TelegramUserMessagesSendable) {
    fun processSms(sms: Sms) {
        // попробуем найти в сообщении все последовательности из 3х и более цифр
        // в телеграмм сообщении заменим их разметкой
        // чтобы на них можно было нажать и сразу скопировать (с помощью markdown)

        val message = arrayListOf(
            sms.address
                .trim(),
            System.lineSeparator(),
            System.lineSeparator(),
            sms.text
                .replace(Regex("\\d{3,}")) { "<code>${it.value}</code>" }
                .trim()
        )
            .joinToString("")

        telegramUserMessagesSendable.sendMessageToUser(message)
    }
}