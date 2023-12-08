package com.example.notedApp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.notedApp.databinding.FragmentReserverdClassDialogBinding
import com.squareup.picasso.Picasso
import java.util.*

class ReservedClassDialogFragment : DialogFragment() {
    lateinit var binding:FragmentReserverdClassDialogBinding
    val args: ReservedClassDialogFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.zoomLinkTV.text = args.zoomLing
        Picasso.with(context).load(args.image).into(binding.regTutorImg)
        binding.timeDurationTV.text = args.time
        binding.withWhoDateTV.text = args.nameDate
        binding.zoomLinkTV.setOnClickListener { openWebPage(args.zoomLing) }

        val day = args.inHowLong.toInt()

        val countdownStart = Calendar.getInstance()
        val startTime = countdownStart.timeInMillis
        val countdownEnd = Calendar.getInstance()
        countdownEnd.add(Calendar.DATE, day)
        countdownEnd.set(Calendar.MINUTE, 0)
        countdownEnd.set(Calendar.SECOND, 0)
        countdownEnd.set(Calendar.MILLISECOND, 0)
        countdownEnd.set(Calendar.HOUR_OF_DAY, HomeActivity.hourTo24Hour(args.hour))
        val endTime = countdownEnd.timeInMillis
        val diffTime = endTime - startTime
        println("countdownStart = ${countdownStart.time}\ncountdownEnd= " +
                "${countdownEnd.time}\ndiffTime = $diffTime" +
                "\nstartTime = $startTime\n" +
                "endTime = $endTime")

        val countdown = object: CountDownTimer(diffTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
                var time = millisUntilFinished
                val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(time)
                time -= java.util.concurrent.TimeUnit.DAYS.toMillis(days)

                val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(time)
                time -= java.util.concurrent.TimeUnit.HOURS.toMillis(hours)

                val mins = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(time)
                time -= java.util.concurrent.TimeUnit.MINUTES.toMillis(mins)

                val sec = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(time);

                binding.inHowLongTV.text = "Class In: $days days, $hours hrs, $mins mins, $sec secs"
            }

            override fun onFinish() {
                binding.inHowLongTV.text = "Class Has BEGUN"
            }

        }
        countdown.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReserverdClassDialogBinding.inflate(inflater, container, false)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        requireActivity().startActivity(intent)
    }


}