package com.group1.mapd721_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class RemainderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: "Medicine"
        val medicineId = intent.getStringExtra("medicine_id")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("medicine_channel", "Medicine Reminder", NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    description = "Time to take your medicine"
                }
            notificationManager.createNotificationChannel(channel)
        }
        val notification = android.app.Notification.Builder(context, "medicine_channel")
            .setContentTitle("Medicine Reminder")
            .setContentText("Time to take your $medicineName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        notificationManager.notify(medicineId.hashCode(), notification)
    }


}