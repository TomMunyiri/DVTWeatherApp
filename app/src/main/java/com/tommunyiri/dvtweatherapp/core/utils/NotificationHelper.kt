package com.tommunyiri.dvtweatherapp.core.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.presentation.MainActivity
import timber.log.Timber

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class NotificationHelper(private val message: String, private val context: Context) {
    private val channelID = "DVT Weather Channel ID"
    private val notificationID = 123

    @SuppressLint("MissingPermission")
    fun createNotification() {
        createNotificationChannel()

        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        runCatching {
            val notification =
                NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.ic_dvt_weather_notification)
                    .setContentTitle(message)
                    .setContentText("Check out the latest weather information in your location!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE,
                        ),
                    )
                    .build()
            NotificationManagerCompat.from(context).notify(notificationID, notification)
        }.onFailure {
            Timber.d("Notifications Error: ${it.localizedMessage}")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelID
            val descriptionText = "Weather Update"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(channelID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
