package com.lis355.retand

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.core.app.ActivityCompat
import com.lis355.retand.ui.theme.RetAndTheme
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class Settings(private var context: Context) {
    companion object {
        const val SHARED_PREFERENCE_NAME = "SharedPreferences"
        const val TELEGRAM_BOT_API_SETTING_NAME = "telegramBotApiToken"
        const val TELEGRAM_USER_ID_SETTING_NAME = "telegramUserId"
    }

    private var sharedPreferences: SharedPreferences =
        this.context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    var telegramBotApiToken: String
        get() = this.sharedPreferences.getString(TELEGRAM_BOT_API_SETTING_NAME, "")!!
        set(value) {
            this.sharedPreferences.edit().putString(TELEGRAM_BOT_API_SETTING_NAME, value).apply()
        }

    var telegramUserId: String
        get() = this.sharedPreferences.getString(TELEGRAM_USER_ID_SETTING_NAME, "")!!
        set(value) {
            this.sharedPreferences.edit().putString(TELEGRAM_USER_ID_SETTING_NAME, value).apply()
        }
}

class SMSReceiver : BroadcastReceiver() {
    internal interface Listener {
        fun onTextReceived(text: String)
    }

    private var TAG = "SmsBroadcastReceiver"

    lateinit var serviceProviderNumber: String
    lateinit var serviceProviderSmsCondition: String

    private var listener: Listener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender = ""
            var smsBody = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.displayOriginatingAddress
                    smsBody += smsMessage.messageBody
                }
            } else {
                val smsBundle = intent.extras
                if (smsBundle != null) {
                    val pdus = smsBundle.get("pdus") as Array<Any>
                    if (pdus == null) {
                        // Display some error to the user
                        e(TAG, "SmsBundle had no pdus key")
                        return
                    }
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    for (i in messages.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        smsBody += messages[i]!!.getMessageBody()
                    }
                    smsSender = messages[0]!!.getOriginatingAddress().toString()
                }
            }
            e(TAG, smsBody)
//            var settingsManager = SettingsManager(context)
//
//            PostReceivedMessage().execute(settingsManager.receiveURL, settingsManager.deviceId, smsBody, smsSender)


            val i = Intent("SMS_RECEIVED")
            // Data you need to pass to activity
            i.putExtra("number", smsSender)
            i.putExtra("message", smsBody)
            context.sendBroadcast(i)

            if (::serviceProviderNumber.isInitialized && smsSender == serviceProviderNumber && smsBody.startsWith(
                    serviceProviderSmsCondition
                )
            ) {
                if (listener != null) {
                    listener!!.onTextReceived(smsBody)
                }
            }
        }
    }
}

class SendMessageResponse

interface TelegramAPI {
    @POST("sendMessage")
    fun sendMessage(@Body body: MutableMap<String, Any>): Call<SendMessageResponse?>?
}

class MainActivity : ComponentActivity() {
    private lateinit var settings: Settings
    private val appPermission = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_MMS)
    private val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10
    private val RECEIVED_SMS_FLAG = "SMS_RECEIVED"
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.flags
            val b = intent.extras
            val number = b!!.getString("number")
            val message = b.getString("message")
            Log.d("App", "Message received and posted from: $number - text: $message")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        enableEdgeToEdge()

        this.settings = Settings(this)

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

//        sendSmsNotification("TEST")

        requestSMSReadPermission()
        registerReceiver(broadcastReceiver, IntentFilter(RECEIVED_SMS_FLAG), RECEIVER_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun requestSMSReadPermission() {
        //        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
        //            // You may display a non-blocking explanation here, read more in the documentation:
        //            // https://developer.android.com/training/permissions/requesting.html
        //        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            MY_PERMISSIONS_REQUEST_SMS_RECEIVE
        )
    }

    fun sendSmsNotification(text: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot${settings.telegramBotApiToken}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val telegramApi: TelegramAPI = retrofit.create(TelegramAPI::class.java)
        val sendMessageBodyMap: MutableMap<String, Any> = HashMap()
        sendMessageBodyMap["chat_id"] = settings.telegramUserId
        sendMessageBodyMap["text"] = text

        telegramApi.sendMessage(sendMessageBodyMap)?.execute()
    }
}

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
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            label = { Text("Telegram Bot Api Token") },
            value = telegramBotApiTokenSetting,
            onValueChange = {
                telegramBotApiTokenSetting = it

                setTelegramBotApiTokenSetting(telegramBotApiTokenSetting)
            },
            modifier = Modifier
                .padding(all = 5.dp)
        )

        TextField(
            label = { Text("Telegram User Id") },
            value = telegramUserIdSetting,
            onValueChange = {
                telegramUserIdSetting = it

                setTelegramUserIdSetting(telegramUserIdSetting)
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
            getTelegramBotApiTokenSetting = { "TOKEN" },
            setTelegramBotApiTokenSetting = { },
            getTelegramUserIdSetting = { "USER ID" },
            setTelegramUserIdSetting = { }
        )
    }
}
