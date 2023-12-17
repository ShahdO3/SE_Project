package com.example.notedApp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.notedApp.databinding.FragmentAddReminderBinding
import com.example.notedApp.databinding.FragmentRemindersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class reminders : Fragment(), AddReminderFragment.dialogSubmitBtnClickListener {



    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentRemindersBinding
    private lateinit var popUpFragment: AddReminderFragment



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRemindersBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View){

        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child(auth.currentUser?.uid.toString()).child("Reminder")

    }


    private fun registerEvents(){
        binding.fAbtn.setOnClickListener {

            popUpFragment = AddReminderFragment()
            popUpFragment.setListener(this)


        }

    }

    override fun onSaveTask(reminder: String, date: String, remindertxt: EditText) {
        TODO("Not yet implemented")
    }
}