package com.example.projectmanagementtool.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.projectmanagementtool.MainActivity
import com.example.projectmanagementtool.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){
    //data messages are handled here in this function
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("Firebase notification","From : ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let{
            Log.d("Firebase remoteMessage","message Data payload : ${remoteMessage.data}")
        }
        remoteMessage.notification?.let{
            Log.d("Notification Body","message notification body : ${it.body}")
        }
    }

    //this function is the place where you will retrieve the token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Firebase Token","application token : ${token}")
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token : String?){
        //Implement the functionality to send the token to the firebase server
    }
    //this function will be called when sending the notification to the application
    private fun sendNotification(messageBody : String){
        //the user should be send to the main activity when the user clicks on the notification
        val intent = Intent(this,MainActivity::class.java)
        //this will make sure the specific activity is at specific position inside of the application stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        /*
        It's possible that the user is on another application when the notification pops up and we cannot use intent to open up the
        MainActivity from another application so we have to use pending intent here
        FLAG_ONE_SHOT says that this intent or this activity should be only used once
         */
        val pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_ONE_SHOT)
        //create a channel ID
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app)
            .setContentTitle("Title")
            .setContentText("Message")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Channel Task Master title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}