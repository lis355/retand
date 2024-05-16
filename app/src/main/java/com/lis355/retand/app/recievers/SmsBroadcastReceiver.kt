package com.lis355.retand.app.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.lis355.retand.app.settings.Settings
import com.lis355.retand.app.sms.Sms
import com.lis355.retand.app.sms.SmsProcessor
import com.lis355.retand.app.telegram.TelegramUserMessagesManager

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action === Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            for (message in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if (message.originatingAddress != null &&
                    message.messageBody != null
                ) {
                    val settings = Settings(context)
                    val telegramUserMessagesManager = TelegramUserMessagesManager(settings)
                    val smsProcessor = SmsProcessor(telegramUserMessagesManager)
                    smsProcessor.processSms(Sms(message.originatingAddress!!, message.messageBody))
                }
            }
        }
    }
}