package com.example.notedApp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.notedApp.databinding.FragmentAddReminderBinding


class AddReminderFragment : Fragment() {

    private lateinit var binding:FragmentAddReminderBinding
    private lateinit var listener: dialogSubmitBtnClickListener

    fun setListener(listener: dialogSubmitBtnClickListener){
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }

    private fun registerEvents(){

        binding.SubmitBtn.setOnClickListener {

            val reminder = binding.Remindertxt.text.toString()

            val date = binding.calendarView2.date.toString()

            if (reminder.isNotEmpty() && date.isNotEmpty()){

                listener.onSaveTask(reminder,date,binding.Remindertxt)
                
            }else{
                Toast.makeText(context, "Empty inputs", Toast.LENGTH_SHORT).show()
            }


        }
    }

    interface dialogSubmitBtnClickListener{

        fun onSaveTask(reminder: String, date: String,  remindertxt : EditText)
    }

}