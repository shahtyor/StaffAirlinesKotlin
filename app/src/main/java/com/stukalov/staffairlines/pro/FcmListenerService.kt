package com.stukalov.staffairlines.pro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Dictionary
import kotlin.random.Random

class FcmListenerService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        GlobalStuff.Token = token

        val SM = StaffMethods()
        SM.SaveAppToken()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle data payload of FCM messages.
        if (remoteMessage.data.isNotEmpty()) {
            // Handle the data message here.
            GlobalStuff.NotificationData = remoteMessage.data
        }

        // Handle notification payload of FCM messages.
        remoteMessage.notification?.let {
            // Handle the notification message here.
            SendNotification2(it.title!!, it.body!!, remoteMessage.data)
        }
    }

    fun SendNotification2(title: String, messageBody: String, data: Map<String, String>)
    {
        var intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        data.forEach { it ->
            intent.putExtra(it.key, it.value)
        }

        val u = Random
        var pendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            pendingIntent = PendingIntent.getActivity(this, u.nextInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(this, u.nextInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "staff_airlines_notification_channel")
            .setSmallIcon(R.drawable.ic_plane)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel("staff_airlines_notification_channel", "sa notification channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(u.nextInt(), notificationBuilder.build());
    }
}