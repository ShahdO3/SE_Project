package com.example.notedApp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notedApp.databinding.NotesItemBinding
import com.example.notedApp.databinding.RegisteredLessonsRowBinding
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NotesAdapter (
    var mutableL: MutableList<NotesInfo>,
    val context: Context,
    val activity: Activity)
    : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private var listener: NotesAdapterClicksInterface? = null
    fun setListener(listener: NotesAdapterClicksInterface){
        this.listener = listener
    }
    interface NotesAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(notes: NotesInfo)
        fun onPressNote(note:NotesInfo)
    }
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val title = view.findViewById<TextView>(R.id.notes_title)
            val des = view.findViewById<TextView>(R.id.notesDescription)
            val deleteBtn = view.findViewById<ImageView>(R.id.notesDeleteBtn)
            val noteLayout = view.findViewById<MaterialCardView>(R.id.layoutNote)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                NotesItemBinding.inflate(
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

            holder.deleteBtn.setOnClickListener {
                listener?.onDeleteTaskBtnClicked(mutableL[position])
            }
            holder.noteLayout.setOnClickListener {
                listener?.onPressNote(mutableL[position])
            }
        }
    }
