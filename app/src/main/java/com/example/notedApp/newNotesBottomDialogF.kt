package com.example.notedApp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.notedApp.databinding.FragmentNewNotesBottomDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class newNotesBottomDialogF : Fragment() {
    private lateinit var binding: FragmentNewNotesBottomDialogBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var adapter: ToDoAdapter
    private lateinit var mlist:MutableList<ToDoInfo>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewNotesBottomDialogBinding
            .inflate(inflater, container, false)

        val title = binding.tasktitle.text
        val desc = binding.taskDesc.text

        binding.addTask.setOnClickListener {
            if (title != null && desc != null) {
                if (title.isEmpty()){
                    binding.tasktitle.error = "Title is Required!"
                }
                else{
                    Toast.makeText(context, "Title must be filled in",
                        Toast.LENGTH_SHORT).show()
                }
                if (desc.isEmpty()){
                    binding.taskDesc.error = "Description is Required!"
                }
                else{
                    Toast.makeText(context, "Description must be filled in",
                        Toast.LENGTH_SHORT).show()
                }

                val note = NotesInfo(title.toString(), desc.toString())
                databaseRef.push().setValue(note).addOnCompleteListener {
                    if (it.isSuccessful){

                        binding.tasktitle.text = null
                        binding.taskDesc.text = null
                        val move = NewNotesFragmentDirections
                            .actionNewNotesFragmentToProfilePageFragment()
                        findNavController().navigate(move)
                    }else{
                        Toast.makeText(context,
                            "Failed to add Note to Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child(auth.currentUser?.uid.toString()).child("Notes")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}