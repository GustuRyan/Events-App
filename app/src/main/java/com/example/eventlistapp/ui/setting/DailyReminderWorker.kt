package com.example.eventlistapp.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventlistapp.R
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        createNotificationChannel()

        // Only proceed if reminder setting is enabled
        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        val isReminderActive = pref.getReminderSetting().first()
        if (!isReminderActive) return Result.success()

        // Fetch the nearest active event
        val event = fetchNearestActiveEvent()
        if (event != null) {
            showNotification(event)
        }

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelId = "DailyReminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Daily Event Reminder"
            val channelDescription = "Channel for daily event reminders"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun fetchNearestActiveEvent(): Event? {
        val response = ApiService.create().getEventsLimit(active = -1, limit = 1) // Adjust active filter as necessary
        return if (response.isSuccessful) {
            response.body()?.listEvents?.firstOrNull()
        } else {
            null
        }
    }

    private fun showNotification(event: Event) {
        // Log the event name for debugging
        Log.d("DailyReminderWorker", "Showing notification for event: ${event.name}")

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "DailyReminder")
            .setContentTitle("Upcoming Event Reminder")
            .setContentText("${event.name} at ${event.beginTime}")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Send the notification
        notificationManager.notify(event.id.hashCode(), notification)
    }

}