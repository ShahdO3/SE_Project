package com.example.notedApp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedApp.databinding.FragmentToDoListBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*


class ToDoListFragment : Fragment(), ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth:FirebaseAuth
    private lateinit var databaseRef:DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentToDoListBinding
    private lateinit var adapter: ToDoAdapter
    private lateinit var mlist:MutableList<ToDoInfo>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        binding = FragmentToDoListBinding
            .inflate(inflater, container,false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        init(view)
        getDataFromFirebase()
        RegisterEvents()
    }


    private fun RegisterEvents(){
//
//        binding.reminderBtn.setOnClickListener {
//            //navController.navigate(R.id.(from todo_to_reminders))
//        }
//
//        binding.NotesBtn.setOnClickListener {
//            //navController.navigate(R.id.(from todo_to_notes))
//        }


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.notes ->{
                    binding.fAbtn.show()
                    val move = ToDoListFragmentDirections
                        .actionToDoListFragment2ToProfilePageFragment()
                    findNavController().navigate(move)
                }
                R.id.reminders->{
                    binding.fAbtn.show()
                }
                R.id.todolist->{
                    binding.fAbtn.hide()

                }
            }
            true
        }

        binding.SubmitBtn.setOnClickListener {

            val task = binding.Tasktxt.text.toString()

            if (task.isNotEmpty()){

                databaseRef.push().setValue(task).addOnCompleteListener{

                    if (it.isSuccessful){

                        binding.Tasktxt.text = null

                    }else{
                        Toast.makeText(context, "failed to create task", Toast.LENGTH_SHORT).show()
                    }
                }
                
            }else{
                Toast.makeText(context, "No task typed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view: View){

        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child(auth.currentUser?.uid.toString()).child("TODO")

        binding.ToDoRecycler.setHasFixedSize(true)
        binding.ToDoRecycler.layoutManager = LinearLayoutManager(context)
        mlist = mutableListOf()
        adapter = ToDoAdapter(mlist)
        adapter.setListener(this)
        binding.ToDoRecycler.adapter = adapter
    }


    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                for (taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let{
                        ToDoInfo(it,taskSnapshot.value.toString())
                    }

                    if(todoTask != null){
                        mlist.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDeleteTaskBtnClicked(toDoInfo: ToDoInfo) {
        databaseRef.child(toDoInfo.taskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


}