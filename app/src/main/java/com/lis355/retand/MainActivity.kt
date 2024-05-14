package com.lis355.retand

import android.Manifest
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.lis355.retand.ui.App
import com.lis355.retand.ui.theme.RetAndTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_PERMISSION_REQUEST_CODE = 10
    }

    private lateinit var settings: Settings
    private lateinit var telegramUserMessagesManager: TelegramUserMessagesManager
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var smsProcessor: SmsProcessor

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureThreadPolicyForHTTP()
        createSettings()
        createTelegramUserMessagesManager();
        registerSmsReceiving();

        createContent();
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterSmsReceiving()
    }

    private fun configureThreadPolicyForHTTP() {
        // for retrofit http requests
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun createSettings() {
        settings = Settings(this)
    }

    private fun createTelegramUserMessagesManager() {
        telegramUserMessagesManager = TelegramUserMessagesManager(settings)
    }

    private fun registerSmsReceiving() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            REQUEST_PERMISSION_REQUEST_CODE
        )

        smsBroadcastReceiver = SmsBroadcastReceiver {
            smsProcessor.processSms(it)
        }

        smsProcessor = SmsProcessor(telegramUserMessagesManager)

        registerReceiver(smsBroadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }

    private fun unregisterSmsReceiving() {
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun createContent() {
        setContent {
            RetAndTheme {
                App(
                    getTelegramBotApiTokenSetting = { this.settings.telegramBotApiToken },
                    setTelegramBotApiTokenSetting = { this.settings.telegramBotApiToken = it },
                    getTelegramUserIdSetting = { this.settings.telegramUserId },
                    setTelegramUserIdSetting = { this.settings.telegramUserId = it }
                )
            }
        }
    }
}
