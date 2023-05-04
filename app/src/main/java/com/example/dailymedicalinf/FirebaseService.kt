package com.example.dailymedicalinf

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.internal.notify
import kotlin.random.Random

const val CHANNEL_ID = "notification channel"
const val CHANNEL_NAME = "com.example.applicationprofile"


class FirebaseService : FirebaseMessagingService() {

    //generate the notification
    //attach the notification created with the custom layout
    //show the notification

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getNotification() !=null){
            generateNotification(remoteMessage.notification!!.title!! , remoteMessage.notification!!.body!!)
        }
    }

    fun getRemoteView(title: String , message: String):RemoteViews{
        val remoteView = RemoteViews("com.example.applicationprofile" , R.layout.notification)
        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.message,message)
        remoteView.setImageViewResource(R.id.applogo,R.drawable.add)

        return remoteView
    }

   fun generateNotification(title:String , message:String){
       val intent = Intent(this , patientMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this , 0,intent,PendingIntent.FLAG_ONE_SHOT)

        //channel id , channel name
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext , CHANNEL_ID)
            .setSmallIcon(R.drawable.add)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title , message))

       val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           val notificationChannel = NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_HIGH)
           notificationManager.createNotificationChannel(notificationChannel)

       }
       notificationManager.notify(0 , builder.build())

   }

}