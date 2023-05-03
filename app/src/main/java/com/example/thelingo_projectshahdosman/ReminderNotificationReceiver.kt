package com.example.thelingo_projectshahdosman

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar


class ReminderNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val builder = NotificationCompat.Builder(context!!.applicationContext, channel_id)
        val tapIntent = Intent(context, HomeActivity::class.java)
        tapIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingTapIntent =  getActivity( context,0,tapIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        val language = intent!!.getStringExtra("lang")
        val tutorName = intent.getStringExtra("tutorName")
        val time = intent.getStringExtra("time")

        val channel = NotificationChannel(channel_id, "1",
            NotificationManager.IMPORTANCE_HIGH)
        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.createNotificationChannel(channel)

        builder.setSmallIcon(R.drawable.baseline_doorbell_24)
            .setContentTitle("$language Class Tomorrow")
            .setContentText("Don't forget you have a $language class tomorrow with $tutorName" +
                    " at $time, be ready!")
            .setContentIntent(pendingTapIntent)
            .color = Color.CYAN

        val not = NotificationManagerCompat.from(context.applicationContext)
        if (ActivityCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        not.notify(Calendar.getInstance().timeInMillis.toInt(), builder.build())
    }
}