package com.gownetwork.criptasapp.network.FireBase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gownetwork.criptasapp.CriptasAppActivity
import mx.com.gownetwork.criptas.R

const val channelId = "notification_channel"
const val channelName = "mx.com.gownetwork.criptas"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            it.title?.let { title ->
                it.body?.let { body ->
                    generateNotification(title, body)
                }
            }
        }
        super.onMessageReceived(message)
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title:String, message: String) : RemoteViews{
        val remoteViews = RemoteViews("mx.com.gownetwork.criptas", R.layout.notificacion_general)
        remoteViews.setTextViewText(R.id.itemNombre, title)
        return remoteViews
    }

    fun generateNotification(title:String, message: String){
        val intent = Intent(this, CriptasAppActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo8)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())
    }
}