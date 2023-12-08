package com.example.notedApp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedApp.databinding.FragmentFindTutorsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class FindTutorsFrag : Fragment() {
    lateinit var binding: FragmentFindTutorsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var imageStorageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val args : FindTutorsFragArgs by navArgs()
    private lateinit var mList:MutableList<TutorsData>
    private lateinit var userRef: DatabaseReference
    private lateinit var tutorsAdapter: TutorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        binding = FragmentFindTutorsBinding.inflate(inflater, container, false)
        imageStorageRef = FirebaseStorage.getInstance().getReference("users/${user.uid}")
        userRef = FirebaseDatabase
            .getInstance()
            .getReference("users")
            .child(user.uid)

        binding.findTutorsRv.layoutManager = LinearLayoutManager(context)
        binding.findTutorsRv.setHasFixedSize(true)

        databaseRef = FirebaseDatabase
            .getInstance()
            .getReference("instructors")

        Picasso.with(context).load(user.photoUrl).placeholder(R.drawable.user)
            .into(binding.profilePic)
        mList = mutableListOf()
        tutorsAdapter = TutorsAdapter(mList, requireContext())
//        databaseRef.get().addOnSuccessListener {snapshot->
//            if (snapshot.exists()){
//                mList.clear()
//
//                for (snap in snapshot.children){
//                    Log.d("items in instructors", snap.value.toString())
//                    val tasksClass = snap.getValue(TutorsData::class.java)
//                    if (tasksClass!=null){
//                        mList.add(tasksClass)
//                        Log.d("worked", tasksClass.languages!!.values.toString())
//                        tutorsAdapter = TutorsAdapter(mList, requireContext())
//                        binding.findTutorsRv.adapter = tutorsAdapter
//                    }
//                }
//            }
//        }
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    mList.clear()

                    for (snap in snapshot.children){
                        Log.d("items in instructors", snap.value.toString())
                        val tasksClass = snap.getValue(TutorsData::class.java)
                        if (tasksClass!=null){
                            mList.add(tasksClass)
                            Log.d("worked", tasksClass.languages!!.values.toString())
                            tutorsAdapter = TutorsAdapter(mList, context!!)
                            binding.findTutorsRv.adapter = tutorsAdapter
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.searchForTutors.clearFocus()

        binding.searchForTutors.setOnQueryTextListener(object :androidx.appcompat.widget.
        SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tutorsAdapter.filter.filter(newText)
                return false
            }

        })

        binding.profilePic.setOnClickListener {
            val move = FindTutorsFragDirections.
            actionFindTutorsFragToProfilePageFragment(args.imageUrl)
            findNavController().navigate(move)
        }
        return binding.root
    }


}