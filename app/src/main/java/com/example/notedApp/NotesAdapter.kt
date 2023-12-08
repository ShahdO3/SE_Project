package com.example.notedApp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notedApp.databinding.RegisteredLessonsRowBinding
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NotesAdapter (
    var mutableL: MutableList<NotesInfo>,
    val context: Context,
    val activity: Activity,
    val fragmentManager: FragmentManager)
    : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val title = view.findViewById<TextView>(R.id.notes_title)
            val des = view.findViewById<TextView>(R.id.notesDescription)
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

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = mutableL[position].title
            holder.des.text = mutableL[position].description

//            holder.itemView.setOnClickListener {
//                val move = ProfilePageFragmentDirections
//                    .actionProfilePageFragmentToReservedClassDialogFragment(
//                        day.toString() , holder.with_who.text.toString(),
//                        tutor.image!!, holder.time_duration.text.toString(),
//                        tutor.zoom!!, current.hourChosen!!)
//                activity.findNavController(R.id.fragmentContainerView).navigate(move)
//            }
        }
    }
