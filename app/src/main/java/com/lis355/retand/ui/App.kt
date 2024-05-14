package com.lis355.retand.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lis355.retand.ui.theme.RetAndTheme

@Composable
fun App(
    getTelegramBotApiTokenSetting: () -> String,
    setTelegramBotApiTokenSetting: (String) -> Unit,
    getTelegramUserIdSetting: () -> String,
    setTelegramUserIdSetting: (String) -> Unit
) {
    var telegramBotApiTokenSetting: String by remember { mutableStateOf(getTelegramBotApiTokenSetting()) }
    var telegramUserIdSetting: String by remember { mutableStateOf(getTelegramUserIdSetting()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            label = { Text("Telegram Bot Api Token") },
            value = telegramBotApiTokenSetting,
            onValueChange = {
                telegramBotApiTokenSetting = it

                setTelegramBotApiTokenSetting(telegramBotApiTokenSetting)
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text("Telegram User Id") },
            value = telegramUserIdSetting,
            onValueChange = {
                telegramUserIdSetting = it

                setTelegramUserIdSetting(telegramUserIdSetting)
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        OutlinedButton(
            onClick = {
                telegramBotApiTokenSetting = ""

                setTelegramBotApiTokenSetting(telegramBotApiTokenSetting)

                telegramUserIdSetting = ""

                setTelegramUserIdSetting(telegramUserIdSetting)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Clear")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    RetAndTheme {
        App(
            getTelegramBotApiTokenSetting = { "TOKEN" },
            setTelegramBotApiTokenSetting = { },
            getTelegramUserIdSetting = { "USER ID" },
            setTelegramUserIdSetting = { }
        )
    }
}
