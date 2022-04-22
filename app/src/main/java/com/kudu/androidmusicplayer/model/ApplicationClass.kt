package com.kudu.androidmusicplayer.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {

    companion object {
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "Now Playing", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is an important channel for showing songs"

            //for lockscreen
  //          notificationChannel.importance = NotificationManager.IMPORTANCE_HIGH
//            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC


            //making notification channel for runtime
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}