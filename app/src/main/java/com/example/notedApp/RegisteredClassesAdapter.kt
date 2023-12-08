package com.example.notedApp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notedApp.databinding.RegisteredLessonsRowBinding
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController

class RegisteredClassesAdapter(
    var mutableL: MutableList<RegisteredClassesInfo>,
    val context: Context,
    val activity: Activity,
    val fragmentManager: FragmentManager)
    : RecyclerView.Adapter<RegisteredClassesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val time_left = view.findViewById<TextView>(R.id.in_how_long_TV)
        val with_who = view.findViewById<TextView>(R.id.with_who_date_TV)
        val time_duration = view.findViewById<TextView>(R.id.time_duration_TV)
        val tutorImage = view.findViewById<CircleImageView>(R.id.reg_tutor_img)
        val zoomLink = view.findViewById<TextView>(R.id.zoom_link_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RegisteredLessonsRowBinding.inflate(
                LayoutInflater
                .from(parent.context), parent, false).root)
    }

    override fun getItemCount(): Int {
        return mutableL.size
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutor = mutableL[position].tutorsData!!
        val current = mutableL[position]

        val dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dayChosen = HomeActivity.dayToNum(current.dayChosen!!.subSequence(0,3) as String)
        var day = 0
        if (dayToday != dayChosen && dayToday > dayChosen){
            day = (dayChosen + 7)- dayToday
            holder.time_left.text = "IN $day DAYS"
        }
        else if (dayToday == dayChosen)
            holder.time_left.text = "TODAY"
        else {
            day = dayChosen - dayToday
            holder.time_left.text = "IN $day DAYS"
        }

        println("day chosen = $dayChosen, day today = $dayToday")

        val amOrPm:String = if (current.hourChosen == 12 || current.hourChosen!! / 10 == 0) "PM"
                            else "AM"


        Picasso.with(context).load(Uri.parse(tutor.image)).into(holder.tutorImage)
        holder.with_who.text = "${current.langChosen} with " +
                "${tutor.name!!.substringBefore(" ")}, ${current.dayChosen}"
        holder.time_duration.text = "${current.hourChosen}:00 $amOrPm, 1 hour"
        holder.zoomLink.text = tutor.zoom

            holder.itemView.setOnClickListener {
                val move = ProfilePageFragmentDirections
                    .actionProfilePageFragmentToReservedClassDialogFragment(
                        day.toString() , holder.with_who.text.toString(),
                        tutor.image!!, holder.time_duration.text.toString(),
                        tutor.zoom!!, current.hourChosen!!)
                activity.findNavController(R.id.fragmentContainerView).navigate(move)
            }
    }
    fun updateUpcomingL(list:MutableList<RegisteredClassesInfo>){
        mutableL = list
        println("size list = ${mutableL.size}")
        notifyDataSetChanged()
    }
}