package com.example.notedApp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.example.notedApp.databinding.FindTutorsRowBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class TutorsAdapter (
    var mutableL: MutableList<TutorsData>,
    val context: Context
)
    : RecyclerView.Adapter<TutorsAdapter.ViewHolder>() , Filterable{

    private var fullList :ArrayList<TutorsData> = ArrayList(mutableL)
    private var filterList = ArrayList<TutorsData>()
    init {
        filterList = mutableL as ArrayList<TutorsData>
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val row: MaterialCardView = view.findViewById(R.id.tutor_row)
        val instructorName:TextView = view.findViewById(R.id.instructor_name_TV)
        val teaches:TextView = view.findViewById(R.id.teaches_TV)
        val availability:TextView = view.findViewById(R.id.availability_TV)
        val tutorImage:ShapeableImageView = view.findViewById(R.id.tutor_pic)
        val langForRates:TextView = view.findViewById(R.id.lang_rates_TV)
        val ratesPerLang:TextView = view.findViewById(R.id.rates_TV)
        val cardView:CardView = view.findViewById(R.id.tutor_row)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FindTutorsRowBinding.inflate(LayoutInflater
            .from(parent.context), parent, false).root)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentInstructor = filterList[position]

        holder.cardView.startAnimation(android.view.animation.AnimationUtils
            .loadAnimation(holder.cardView.context, dreamers.graphics.R.anim.abc_grow_fade_in_from_bottom))
        holder.instructorName.text = currentInstructor.name
        Picasso.with(context).load(Uri.parse(currentInstructor.image)).into(holder.tutorImage)
        holder.tutorImage.clipToOutline = true
        var langs = ""
        var ratesLang= ""
        var rates=""

        for(lang in currentInstructor.languages!!.values){
            langs+= "$lang, "
            ratesLang+= "$lang: \n"
        }
        langs = langs.dropLast(2)

        holder.teaches.text = langs
        holder.langForRates.text = ratesLang

        for(rate in currentInstructor.rates!!.values){
            rates+= "USD $rate/hour\n"
        }
        holder.ratesPerLang.text = rates

        val daysAvailArr = currentInstructor.availability!!.day!!
        val timeAvail = currentInstructor.availability!!.time!!

        val availString = "${daysAvailArr["d1"]}- " +
                "${daysAvailArr["dn"]} from " +
                "${timeAvail["start"]}:00 - ${timeAvail["end"]}:00 PM"

        holder.availability.text = availString

        holder.row.setOnClickListener {
            val intent = Intent(context, TutorInfoAndBookingActivity::class.java)
            intent.putExtra("tutorImage", currentInstructor.image!!)
            intent.putExtra("tutorInfo", currentInstructor)
            intent.putExtra("teaches", "Teaches: $langs")
            intent.putExtra("rates", rates)
            intent.putExtra("ratesLangs", ratesLang)

            context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        return searchFilter
    }
    private val searchFilter:Filter = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val text = constraint.toString()
            if (text.isEmpty())
                filterList = mutableL as ArrayList<TutorsData>
            else {
                val newList = ArrayList<TutorsData>()
                if (constraint!!.isNotEmpty()) {
                    val newText = text.lowercase(Locale.ROOT).trim()
                    for (tutor in mutableL) {

                        //---------SEARCHING BY LANGUAGE--------------
                        for (lang in tutor.languages!!.values) {
                            if (lang.lowercase(Locale.ROOT).contains(newText))
                                newList.add(tutor)
                        }

                        //---------SEARCHING BY DAY--------------
                        for (day in tutor.availability!!.day!!.values) {
                            if (newText.contains(day.lowercase(Locale.ROOT)))
                                newList.add(tutor)
                            else if (day.lowercase(Locale.ROOT).contains(newText))
                                newList.add(tutor)
                        }
                        if (newText.isDigitsOnly()) {
                            val num = newText.toInt()

                            //---------SEARCHING BY RATE--------------
                            for (rate in tutor.rates!!.values) {
                                if (num == rate)
                                    newList.add(tutor)
                            }

                            //---------SEARCHING BY HOUR--------------
                            for (hour in tutor.slots!!.values.first()) {
                                if (num > 12) {
                                    if (num == hour)
                                        newList.add(tutor)
                                } else {
                                    if (HomeActivity.hourTo24Hour(num) == hour)
                                        newList.add(tutor)
                                }
                            }
                        }
                    }
                    println("text not empty")
                    println(newList.size)
                    println(fullList.size)
                    filterList = newList
                }
            }
            val results = FilterResults()
            results.values = filterList
            println("res size = ")
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filterList = results!!.values as java.util.ArrayList<TutorsData>
            println("publishing")
            notifyDataSetChanged()
        }

    }


}