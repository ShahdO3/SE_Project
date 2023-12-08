package com.example.notedApp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.setPadding
import com.addisonelliott.segmentedbutton.SegmentedButton
import com.example.notedApp.databinding.FragmentTutorInfoAndBookingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class TutorInfoAndBookingActivity : AppCompatActivity() {
    val channel_id = "1"
    lateinit var binding: FragmentTutorInfoAndBookingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var instructor: DatabaseReference
    private lateinit var slotsRef: DatabaseReference
    lateinit var sharedPref: SharedPreferences

    fun savePositionTaken(tutor:TutorsData, dayPos:Int, hourPos:Int){
        var prev = getDayP(tutor, dayPos)
        if (prev.contains("f")){
            val newList = tutor.slotsReserved!!.toMutableList()
            newList[dayPos] = hourPos.toString()
            instructor.child("slotsReserved").setValue(newList)
        }else{
            prev += " $hourPos"
            val newList = tutor.slotsReserved!!.toMutableList()
            newList[dayPos] = prev
            instructor.child("slotsReserved").setValue(newList)
        }
    }
    fun getDayP(tutor:TutorsData, pos:Int):String{
        return tutor.slotsReserved!![pos]
    }
    private fun setReservedSlots(tutor: TutorsData,pos:Int){
        val getHourPos = getDayP(tutor,pos)
        println("gethours string= $getHourPos")
        if (getHourPos.isNotEmpty()){
            if(getHourPos.contains("f")) {
                println("contains f")
                for (btns in binding.hoursAvailSG.buttons) {
                    btns.setBackground(Color.WHITE)
                    btns.setSelectedBackgroundColor(resources.getColor(R.color.recycler_view_background))
                    btns.isEnabled = true
                    btns.isClickable = true

                }
            }else {
                val hourPos = getHourPos.split(" ")
                val numBtns = binding.hoursAvailSG.buttons.size -1
                for(i in 0..numBtns){
                    val btnH = binding.hoursAvailSG.getButton(i)
                    //Position is reserved
                    if (i.toString() in hourPos) {
                        btnH.setBackground(Color.LTGRAY)
                        btnH.setSelectedBackgroundColor(Color.LTGRAY)
                        btnH.isEnabled = false
                        btnH.isClickable = false
                    } else{
                        btnH.setBackground(Color.WHITE)
                        btnH.setSelectedBackgroundColor(resources.getColor(R.color.recycler_view_background))
                        btnH.isEnabled = true
                        btnH.isClickable = true
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentTutorInfoAndBookingBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase
            .getInstance()
            .getReference("users")
            .child(auth.currentUser!!.uid)

        supportActionBar?.hide()
        setContentView(binding.root)

        Picasso.with(this).load(Uri.parse(intent.getStringExtra("tutorImage")))
            .placeholder(R.drawable.user).into(binding.tutorImg)
        val tutorData = intent.getSerializableExtra("tutorInfo")!! as TutorsData

        instructor = FirebaseDatabase
            .getInstance()
            .getReference("instructors")
            .child(tutorData.name!!)


        instructor.child("slotsReserved").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                tutorData.slotsReserved = snapshot.value as List<String>?
                setReservedSlots(tutorData, binding.daysAvailSG.position)
                val btnH = binding.hoursAvailSG.getButton(binding.hoursAvailSG.position)
                btnH.setBackground(Color.LTGRAY)
                btnH.setSelectedBackgroundColor(Color.LTGRAY)
                btnH.isEnabled = false
                btnH.isClickable = false
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        slotsRef = FirebaseDatabase
            .getInstance()
            .getReference("instructors")
            .child(tutorData.name!!)
            .child("slots")

        binding.tutorBio.text = tutorData.biography
        binding.teaches.text = intent.getStringExtra("teaches")
        binding.ratesLangs.text = intent.getStringExtra("ratesLangs")
        binding.rates.text = intent.getStringExtra("rates")
        binding.tutorName.text = tutorData.name
        val daysAvailSGroup = binding.daysAvailSG
        val hrsAvailSGroup = binding.hoursAvailSG

        for(lang in tutorData.languages!!.values){
            val radioButton = RadioButton(this)
            radioButton.text = lang
            binding.languageRG.addView(radioButton)
        }
        binding.languageRG.check(binding.languageRG[0].id)

        binding.back.setOnClickListener {onBackPressed()}

        //SET THE DAYS AVAIL SEGMENT BUTTON
        val dayMap = HashMap<Int, SegmentedButton>()
        for (day in tutorData.slots!!.keys){
            println("tutor = ${tutorData.name}\n" +
                    "key = $day\n" +
                    "value = ${tutorData.slots!![day]}")
        }
        for(days in tutorData.availability!!.day!!.values){
            val segButton = SegmentedButton(this)

            val dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val dayChosen = HomeActivity.dayToNum(days.subSequence(0,3) as String)

            val daysTill = if (dayToday > dayChosen){
                ((dayChosen + 7)- dayToday)
            } else if(dayToday == dayChosen)
                0
            else
                (dayChosen - dayToday)

            var date = LocalDateTime.now()
            if(dayToday != dayChosen)
                date = date.plusDays(daysTill.toLong())
            val format = DateTimeFormatter.ofPattern("EEE,\nLLL dd")
//            segButton.text = format.format(date)
            segButton.text = days
            segButton.setPadding(20)
            segButton.textSize = 60F
            segButton.textAlignment = TEXT_ALIGNMENT_CENTER
            segButton.selectedTextColor = resources.getColor(R.color.burgundy)
            segButton.textTypeface = resources.getFont(R.font.estedad_black)

            dayMap[HomeActivity.dayToNum(days.subSequence(0, 3) as String)] = segButton
        }
        val sortedMap = dayMap.toSortedMap()
        for (button in sortedMap.values){daysAvailSGroup.addView(button)}

        //SET THE HOURS AVAIL SEGMENT BUTTON
        val start = tutorData.availability!!.time!!["start"]!!
        var end = tutorData.availability!!.time!!["end"]!!

        end = if(end < 12){
            if (start < 12)
                HomeActivity.hourTo12Hour(end)
            else
                HomeActivity.hourTo24Hour(end)
        }else
            HomeActivity.hourTo24Hour(end)

        var time: Int
        for (i in start..end){
            time = HomeActivity.hourTo12Hour(i)
            val segButton = SegmentedButton(this)
            segButton.text = "$time:00"
            segButton.setPadding(20)
            segButton.textSize = 65F
            segButton.textAlignment = TEXT_ALIGNMENT_CENTER
            segButton.selectedTextColor = resources.getColor(R.color.burgundy)
            segButton.textTypeface = resources.getFont(R.font.estedad_black)
            hrsAvailSGroup.addView(segButton)
        }

//        binding.daysAvailSG.isClickable = false
        setReservedSlots(tutorData,0)

        binding.daysAvailSG.setOnPositionChangedListener {setReservedSlots(tutorData,it)}
//        binding.tutorImg.setOnClickListener {
//            val date =  Calendar.getInstance()
//            date.add(Calendar.MINUTE, 1)
//            scheduleNotification(0 ,tutorData.name!!, "Spanish",
//                "$1:00",date)
//        }

        binding.bookBtn.setOnClickListener {
            val hourBtn = binding.hoursAvailSG.getButton(binding.hoursAvailSG.position)
            val hour = hourBtn
                .text
                .toString()
                .substringBefore(":")
                .toInt()
            val dayBtn = binding.daysAvailSG.getButton(binding.daysAvailSG.position)
            val day = dayBtn
                .text
                .toString()
            val langBtn = findViewById<RadioButton>(binding.languageRG.checkedRadioButtonId)
            val lang = langBtn
                .text
                .toString()

            val dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val dayChosen = HomeActivity.dayToNum(day.subSequence(0,3) as String)

// -------Chose a time that already passed ----------------
            if(dayToday == dayChosen &&
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                >= HomeActivity.hourTo24Hour(hour)){
                Toast.makeText(applicationContext, "Time already passed", Toast.LENGTH_LONG).show()
            }
            else if (!hourBtn.isEnabled){
                Toast.makeText(
                    applicationContext,
                    "Slot already Reserved, try another slot!",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Confirm Appointment")
                    .setIcon(R.drawable.baseline_calendar_month_24)
                    .setMessage("Are you sure about your booking with \b${tutorData.name}, " +
                            "for language: $lang on $day at $hour?")
                    .setPositiveButton(Html.fromHtml("<font color='#7A5252'><b>Confirm</b></font>",
                        0)){ _, _ ->
                        val reg = RegisteredClassesInfo(tutorData, lang, day, hour)
                        databaseRef.child("classes")
                            .child(reg.id!!)
                            .setValue(reg).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Class Successfully Registered!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    hourBtn.tag = day
                                    savePositionTaken(tutorData, binding.daysAvailSG.position,
                                        binding.hoursAvailSG.position)
                                    setReservedSlots(tutorData, binding.daysAvailSG.position)


                                    if (dayChosen != dayToday){
                                        val daysTill = if (dayToday > dayChosen){
                                            ((dayChosen + 7)- dayToday) - 1
                                        } else
                                            (dayChosen - dayToday) - 1
                                        val date = Calendar.getInstance()
                                        date.add(Calendar.DATE, daysTill)
                                        date.set(Calendar.MINUTE, 0)
                                        println("daystill = $daysTill")
                                        if (daysTill == 0) {
//           -------------------Class is Tomorrow so reminder set 2 hours from registration---------
                                            date.set(
                                                Calendar.HOUR_OF_DAY,
                                                date.get(Calendar.HOUR_OF_DAY) + 2
                                            )

//                                            date.add(Calendar.MINUTE, 1)
                                            println("in if daytill == 0 and date now is ${date.time}")
                                        }
                                        else {
//           -------------------Setting reminder at same hour a day before the class---------
                                            date.set(
                                                Calendar.HOUR_OF_DAY,
                                                HomeActivity.hourTo24Hour(hour)
                                            )
                                            println("in else and date now is ${date.time}")
                                        }

                                        println("out of both and date now is ${date.time}")
                                        val snack = Snackbar.make(binding.tutorBookingView,
                                            "Reminder Set For ${date.time}",
                                            Snackbar.LENGTH_INDEFINITE)
                                            .setBackgroundTint(resources.getColor(R.color.darker_than_background))
                                            .setActionTextColor(Color.BLACK)
                                            .setTextColor(Color.BLACK)
                                        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                                        snack.setAction("Close"){ snack.dismiss()}
                                        snack.show()

                                        scheduleNotification(daysTill ,tutorData.name!!, lang,
                                            "$hour:00", date)
                                    }else{
                                        val snack = Snackbar.make(binding.tutorBookingView,
                                            "Class today at $hour:00 so no need for Reminder!",
                                            Snackbar.LENGTH_INDEFINITE)
                                            .setBackgroundTint(
                                                resources.getColor(R.color.darker_than_background))
                                            .setActionTextColor(Color.BLACK)
                                            .setTextColor(Color.BLACK)
                                        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                                        snack.setAction("Close"){ snack.dismiss()}
                                        snack.show()
                                    }
                                }
                                else
                                    Toast.makeText(applicationContext,
                                        it.exception?.localizedMessage.toString(),
                                        Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setCancelable(false)
                    .setNegativeButton(Html.fromHtml("<font color='#7A5252'>Cancel</font>",
                        0)){dialog, _ -> dialog.dismiss()}
                    .show()
            }
        }


    }

    private fun scheduleNotification(howManyDays:Int, tutorName:String, lang:String, time:String,
                                     date: Calendar){
        val intent = Intent(applicationContext, ReminderNotificationReceiver::class.java)
        intent.putExtra("lang", lang)
        intent.putExtra("tutorName", tutorName)
        intent.putExtra("time", time)

        println(date.time)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            date.timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            date.timeInMillis,
            pendingIntent)

        val snack = Snackbar.make(binding.tutorBookingView, "Reminder Set For ${date.time}",
            Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(resources.getColor(R.color.darker_than_background))
            .setActionTextColor(Color.BLACK)
            .setTextColor(Color.BLACK)
        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snack.setAction("Close"){_ -> snack.dismiss()}
        snack.show()

    }


}