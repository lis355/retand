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
        // попробуем найти в сообщении все последовательности из 3х и более цифр, в телеграмм сообщении напишем их после сообщения
        // чтобы на них можно было нажать и сразу скопировать (с помощью markdown)

        val messageLines = arrayListOf(
            sms.address,
            System.lineSeparator(),
            System.lineSeparator(),
            sms.text
        )

        val numberSequences = Regex("\\d{3,}").findAll(sms.text).map { it.value }
        for (numberSequence in numberSequences) {
            messageLines.add(System.lineSeparator())
            messageLines.add(System.lineSeparator())
            messageLines.add("<code>$numberSequence</code>")
        }

        val message = messageLines.joinToString("")

        telegramUserMessagesSendable.sendMessageToUser(message)
    }
}