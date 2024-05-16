package com.lis355.retand.app.sms

import com.lis355.retand.app.network.TelegramUserMessagesSendable

class Sms(val address: String, val text: String)

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