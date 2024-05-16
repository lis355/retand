package com.lis355.retand.app.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.lis355.retand.app.settings.Settings
import com.lis355.retand.ui.App
import com.lis355.retand.ui.theme.RetAndTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_PERMISSION_REQUEST_CODE = 10
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            REQUEST_PERMISSION_REQUEST_CODE
        )

        val settings = Settings(this)

        setContent {
            RetAndTheme {
                App(
                    getTelegramBotApiTokenSetting = { settings.telegramBotApiToken },
                    setTelegramBotApiTokenSetting = { settings.telegramBotApiToken = it },
                    getTelegramUserIdSetting = { settings.telegramUserId },
                    setTelegramUserIdSetting = { settings.telegramUserId = it }
                )
            }
        }
    }
}
