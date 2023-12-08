package com.example.notedApp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

const val channel_id = "1"
const val notifId = 1

class NotificationJobService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val builder = NotificationCompat.Builder(applicationContext, channel_id)

//        val intent2 =

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

            val channel = NotificationChannel(channel_id, "1",
                NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            builder.setSmallIcon(R.drawable.baseline_doorbell_24)
                .setContentTitle("Class Tomorrow")
                .setContentText("Don't forget you have a class tomorrow")
                .color = Color.CYAN

        }else{

            builder.setSmallIcon(R.drawable.baseline_doorbell_24)
                .setContentTitle("GREAT JOB")
                .setContentText("Don't forget you have a class tomorrow")
                .priority = NotificationCompat.PRIORITY_HIGH
        }

        val not = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("here")
//            ActivityCompat.requestPermissions(,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            return
        }
//        Toast.makeText(applicationContext, "reminder set", Toast.LENGTH_SHORT).show()
        val cal = Calendar.getInstance()
        val futureT = Calendar.getInstance()
        futureT.set(Calendar.MINUTE, 2)
//        if (cal.time == futureT.time)
            not.notify(1, builder.build())
    }
    companion object {

        fun backgroundService(context: Context, intent: Intent) {
            enqueueWork(context, NotificationJobService::class.java, 1, intent)
        }
    }

}