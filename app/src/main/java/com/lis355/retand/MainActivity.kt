package com.lis355.retand

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lis355.retand.ui.theme.RetAndTheme

class Settings(private var context: Context) {
    companion object {
        const val SHARED_PREFERENCE_NAME = "SharedPreferences"
        const val SERVER_URL_SETTING_NAME = "url"
    }

    private var sharedPreferences: SharedPreferences =
        this.context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    var serverUrl: String
        get() = this.sharedPreferences.getString(SERVER_URL_SETTING_NAME, "")!!
        set(value) {
            this.sharedPreferences.edit().putString(SERVER_URL_SETTING_NAME, value).apply()
        }
}

class MainActivity : ComponentActivity() {
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        this.settings = Settings(this)

        setContent {
//            val getUrlSetting = { this.settings.serverUrl }
//            val setUrlSetting:

            RetAndTheme {
                App(
                    getUrlSetting = { this.settings.serverUrl },
                    setUrlSetting = { this.settings.serverUrl = it }
                )
            }
        }
    }
}

@Composable
fun App(getUrlSetting: () -> String, setUrlSetting: (String) -> Unit) {
    var urlSetting: String by remember { mutableStateOf(getUrlSetting()) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            label = { Text("Telegram Bot Token") },
            value = urlSetting,
            onValueChange = {
                urlSetting = it

                setUrlSetting(urlSetting)
            },
            modifier = Modifier
                .padding(all = 5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    RetAndTheme {
        App(
            getUrlSetting = { "123" },
            setUrlSetting = { }
        )
    }
}
