package com.example.miercoles

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.miercoles.ui.theme.MiercolesTheme


class MainActivity : ComponentActivity() {
    private lateinit var batteryLevel: MutableState<String>
    private lateinit var isWifiEnabled: MutableState<String>
    private lateinit var isBluetoothEnabled: MutableState<String>

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPercentage = (level * 100 / scale.toFloat()).toInt()
                    batteryLevel.value = "Battery Level: $batteryPercentage%"
                }
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                    isWifiEnabled.value = "WiFi: ${getWifiStateString(wifiState)}"
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    isBluetoothEnabled.value = "Bluetooth: ${getBluetoothStateString(bluetoothState)}"
                }
            }
        }
    }

    private fun getWifiStateString(state: Int): String {
        return when (state) {
            WifiManager.WIFI_STATE_DISABLED -> "Desactivado"
            WifiManager.WIFI_STATE_ENABLED -> "Activado"
            else -> "Desconocido"
        }
    }

    private fun getBluetoothStateString(state: Int): String {
        return when (state) {
            BluetoothAdapter.STATE_OFF -> "Desactivado"
            BluetoothAdapter.STATE_ON -> "Activado"
            else -> "Desconocido"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        batteryLevel = mutableStateOf("")
        isWifiEnabled = mutableStateOf("")
        isBluetoothEnabled = mutableStateOf("")

        createNotificationChannel()

        setContent {
            MiercolesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = batteryLevel.value)
                        Text(text = isWifiEnabled.value)
                        Text(text = isBluetoothEnabled.value)
                        Button(
                            onClick = { sendNotification() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Notificar")
                        }
                        Button(
                            onClick = { sendNotification() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Notificar2")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceivers()
    }

    private fun registerReceivers() {
        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(broadcastReceiver, batteryIntentFilter)

        val wifiIntentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(broadcastReceiver, wifiIntentFilter)

        val bluetoothIntentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(broadcastReceiver, bluetoothIntentFilter)
    }

    private fun unregisterReceivers() {
        unregisterReceiver(broadcastReceiver)
    }

    private fun createNotificationChannel() {
        val channelId = "com.example.miercoles.notification"
        val channelName = "My Channel"
        val channelDescription = "Notification Channel"

        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun sendNotification() {
        val channelId = "com.example.miercoles.notification"
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ide)
            .setContentTitle("Hola")
            .setContentText("${batteryLevel.value}\n${isWifiEnabled.value}\n${isBluetoothEnabled.value}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


}


