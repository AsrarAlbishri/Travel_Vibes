package com.tuwaiq.travelvibes

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification"
const val channelName = "my notification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
      if (remoteMessage.getNotification() != null ){
          generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
      }
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews{

        val remoteViews = RemoteViews("com.tuwaiq.travelvibes",R.layout.notification)

        remoteViews.setTextViewText(R.id.notification_title,title)
        remoteViews.setTextViewText(R.id.notifiy_message,message)
        remoteViews.setImageViewResource(R.id.logo_notification,R.drawable.travel)

        return remoteViews

    }

    fun generateNotification(title:String , message:String){



        val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
        val notificationManager =  this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

       // val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val builder = this.let {
            NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.travel)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000,1000,1000,1000))
                .setOnlyAlertOnce(true)
                .build()
        }

        val nm = this.let {
            NotificationManagerCompat.from(it)
        }

       // builder = builder.setContent(getRemoteView(title,message))



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//        }

        nm.notify(0,builder)
    }
}