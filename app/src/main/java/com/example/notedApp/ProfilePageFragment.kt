package com.example.notedApp

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.notedApp.databinding.FragmentProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ProfilePageFragment : Fragment() {
    private val args : ProfilePageFragmentArgs by navArgs()
    private lateinit var imgUri : Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var user:FirebaseUser
    private lateinit var userRef: DatabaseReference
    private lateinit var imageStorageRef:StorageReference
    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var cont: Context
    lateinit var progress: BeautifulProgressDialog
    private lateinit var adapter: NotesAdapter
    private lateinit var mlist:MutableList<NotesInfo>
    private lateinit var databaseRef:DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.title   = "Notes"


        progress = BeautifulProgressDialog(activity,
            BeautifulProgressDialog.withLottie, "Please wait...")
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)
        progress.setLayoutColor(Color.WHITE)

        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)


        binding = FragmentProfilePageBinding.inflate(inflater, container, false)

        cont = requireContext()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        mlist = mutableListOf()
        adapter = NotesAdapter(mlist, requireContext(), requireActivity())

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


        userRef.child("Notes").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    mlist.clear()
                    for (snap in snapshot.children){

                        val notes = snap.getValue(NotesInfo::class.java)
                        if (notes!=null){
                            println("notes =  $notes")
                            mlist.add(notes)

                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context!!, error.message, Toast.LENGTH_LONG).show()
            }
        })



        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.notes ->{
                    binding.fAbtn.show()
                }
                R.id.reminders->{
                    binding.fAbtn.show()
                }
                R.id.todolist->{
                    binding.fAbtn.hide()
                    val move = ProfilePageFragmentDirections
                        .actionProfilePageFragmentToToDoListFragment2()
                    findNavController().navigate(move)

                }
            }
            true
        }

        binding.fAbtn.setOnClickListener {
//            val move = ProfilePageFragmentDirections
//                .actionProfilePageFragmentToNewNotesBottomDialogF()
//            findNavController().navigate(move)
        }
        return binding.root
    }

    private fun getPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 0)
        }
        else if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            println("permission denied for MANAGE_EXTERNAL_STORAGE")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }

    }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        println("on pause?")
    }

//    private fun retrieveAndSavePic(){
//        picPhoto.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).also {
//            it.type = "image/*"
//            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        })
//
//    }
//
//    private fun displayPic(){
//        println("image uri = ${user.photoUrl}")
//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.READ_MEDIA_IMAGES)
//            == PackageManager.PERMISSION_GRANTED)
//            println("permission granted")
//        Picasso.with(context).load(user.photoUrl).placeholder(R.drawable.user).into(binding.userPp)
//    }

//    private fun galleryIntent() {
//        retrieveAndSavePic()
//        displayPic()
//    }

}