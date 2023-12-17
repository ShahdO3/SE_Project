package com.example.notedApp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.notedApp.databinding.FragmentProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfilePageFragment : Fragment(), NotesAdapter.NotesAdapterClicksInterface {
    private val args: ProfilePageFragmentArgs by navArgs()
    private lateinit var imgUri: Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var userRef: DatabaseReference
    private lateinit var imageStorageRef: StorageReference
    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var cont: Context
    lateinit var progress: BeautifulProgressDialog
    private lateinit var adapter: NotesAdapter
    private lateinit var mlist: MutableList<NotesInfo>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.title = "Notes"


        progress = BeautifulProgressDialog(
            activity,
            BeautifulProgressDialog.withLottie, "Please wait..."
        )
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)
        progress.setLayoutColor(Color.WHITE)

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
        )


        binding = FragmentProfilePageBinding.inflate(inflater, container, false)

        cont = requireContext()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        mlist = mutableListOf()
        adapter = NotesAdapter(mlist, requireContext(), requireActivity())
        adapter.setListener(this)
        binding.notesRecycler.setHasFixedSize(true)
        binding.notesRecycler.layoutManager = LinearLayoutManager(context)
        binding.notesRecycler.adapter = adapter

        imgUri = user.photoUrl!!

        println("recieved image = ${args.imageUri}")

        userRef = FirebaseDatabase
            .getInstance()
            .getReference("users")
            .child(user.uid)
        imageStorageRef = FirebaseStorage.getInstance()
            .getReference("profileImg")
            .child(user.uid)
        databaseRef = FirebaseDatabase.getInstance().reference
            .child(auth.currentUser?.uid.toString()).child("Notes")

//
//        binding.notesRecycler.layoutManager = LinearLayoutManager(
//            context, LinearLayoutManager.VERTICAL, false)
//        binding.notesRecycler.setHasFixedSize(true)


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.notes -> {
                    binding.fAbtn.show()
                }
                R.id.reminders -> {
                    binding.fAbtn.show()
                }
                R.id.todolist -> {
                    binding.fAbtn.hide()
                    val move = ProfilePageFragmentDirections
                        .actionProfilePageFragmentToToDoListFragment2()
                    findNavController().navigate(move)

                }
            }
            true
        }

        binding.fAbtn.setOnClickListener {
            val move = ProfilePageFragmentDirections
                .actionProfilePageFragmentToNewNotesFragment("", "")
            findNavController().navigate(move)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                println("in data change")
                if (snapshot.exists()) {
                    println("snapshots exists")
                    for (snap in snapshot.children) {
                        var title: String? = null
                        var desc: String? = null
                        for (s in snap.children) {
                            if (s.key == "title")
                                title = s.value.toString()
                            else if (s.key == "description")
                                desc = s.value.toString()
                        }

                        val notes = NotesInfo(title, desc)
                        mlist.add(notes)
                        binding.nothingRegTV.visibility = INVISIBLE

                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context!!, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDeleteTaskBtnClicked(notes: NotesInfo) {
//        println("${notes.id}")

        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Deleting ${notes.title}?")
        builder.setMessage("Are you sure you want to delete this note?")
        builder.setPositiveButton(Html.fromHtml("<font color='#7A5252'>Yes</font>", 0))
        { _, _ ->
            databaseRef.child(notes.title!!).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
            .setNegativeButton(Html.fromHtml("<font color='#7A5252'>No</font>", 0))
            {dialog,_->
                dialog.dismiss()
            }
        builder.show()
    }

    override fun onPressNote(note: NotesInfo) {
        val move = ProfilePageFragmentDirections
            .actionProfilePageFragmentToNewNotesFragment(note.title!!, note.description!!)
        findNavController().navigate(move)
    }

}




