package com.lis355.retand

import android.content.Context
import android.content.SharedPreferences

class Settings(private val context: Context) {
    companion object {
        const val SHARED_PREFERENCE_NAME = "SharedPreferences"
        const val TELEGRAM_BOT_API_SETTING_NAME = "telegramBotApiToken"
        const val TELEGRAM_USER_ID_SETTING_NAME = "telegramUserId"
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    var telegramBotApiToken: String
        get() = sharedPreferences.getString(TELEGRAM_BOT_API_SETTING_NAME, "")!!
        set(value) {
            sharedPreferences.edit().putString(TELEGRAM_BOT_API_SETTING_NAME, value).apply()
        }

    var telegramUserId: String
        get() = sharedPreferences.getString(TELEGRAM_USER_ID_SETTING_NAME, "")!!
        set(value) {
            sharedPreferences.edit().putString(TELEGRAM_USER_ID_SETTING_NAME, value).apply()
        }
}