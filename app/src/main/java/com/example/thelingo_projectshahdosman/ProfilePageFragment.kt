package com.example.thelingo_projectshahdosman

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.thelingo_projectshahdosman.databinding.FragmentProfilePageBinding
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
    private lateinit var instructorsRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var imageStorageRef:StorageReference
    private lateinit var upcomingList: MutableList<RegisteredClassesInfo>
    private lateinit var todayList: MutableList<RegisteredClassesInfo>
    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var cont: Context
    lateinit var progress: BeautifulProgressDialog
    val PERMISSION_REQUEST_READ_FOLDERS = 1

    private var picPhoto = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                progress.show()
                imgUri = result.data!!.data!!
                println("here got uri = $imgUri")
                requireActivity().contentResolver.takePersistableUriPermission(imgUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //compressing image before putting in Firebase Storage for faster upload time
                var bmp: Bitmap? = null
                try {
                    bmp = MediaStore.Images.Media.getBitmap(requireActivity()
                        .contentResolver, imgUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()
                bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                val fileInBytes: ByteArray = baos.toByteArray()
                imageStorageRef.putBytes(fileInBytes).addOnCompleteListener{uploadTask->
                    progress.setMessage("Uploading image...")
                    if (uploadTask.isSuccessful){
                        uploadTask.result.storage
                            .downloadUrl.addOnCompleteListener {
                                progress.setMessage("Setting things up...")
                                if (it.isSuccessful){
                                    val img = it.result
                                    userRef.child("imageUri").setValue(img.toString())
                                    val profileUpdates: UserProfileChangeRequest= UserProfileChangeRequest.Builder()
                                        .setPhotoUri(img)
                                        .build()
                                    user.updateProfile(profileUpdates)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful)
                                                Picasso.with(context).load(imgUri).placeholder(R.drawable.user).into(binding.userPp)
                                            else
                                                println("not saved")
                                        }
                                    progress.dismiss()
                                }
                            }
                    }
                }

            }else
                println("result code is n")

        }
    private var permissionLauncher  = registerForActivityResult(ActivityResultContracts
        .RequestPermission()){
            permissionRead = it ?: permissionRead
            println("Permission is: $it")
        if (it == false)
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()

    }
    var permissionRead = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        upcomingList = mutableListOf()
        todayList = mutableListOf()
        cont = requireContext()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        imgUri = user.photoUrl!!
        Picasso.with(context).load(imgUri).placeholder(R.drawable.user).into(binding.userPp)
        println("recieved image = ${args.imageUri}")
//        getPic()
        instructorsRef = FirebaseDatabase
            .getInstance()
            .getReference("instructors")
        userRef = FirebaseDatabase
            .getInstance()
            .getReference("users")
            .child(user.uid)
        imageStorageRef = FirebaseStorage.getInstance()
            .getReference("profileImg")
            .child(user.uid)

        binding.upcomingRecycler.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false)
        binding.upcomingRecycler.setHasFixedSize(true)

        binding.todayRecycler.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false)
        binding.todayRecycler.setHasFixedSize(true)


        userRef.child("classes").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    upcomingList.clear()
                    for (snap in snapshot.children){
                        println("snap = $snap")
                        val classes = snap.getValue(RegisteredClassesInfo::class.java)
                        if (classes!=null){
                            println("classes =  $classes")
                            val dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                            val dayChosen = HomeActivity.dayToNum(classes.dayChosen!!
                                .subSequence(0,3) as String)

                            if (dayChosen == dayToday ) {
                                todayList.add(classes)
                                binding.nothingRegTV.visibility = INVISIBLE
                            }else {
                                upcomingList.add(classes)
                                binding.nothingRegTV2.visibility = INVISIBLE
                            }
                        }
                    }
                    try{
                        val adapter = RegisteredClassesAdapter(upcomingList, context!!, requireActivity(), parentFragmentManager)
                        binding.upcomingRecycler.adapter = adapter
                        val adapter2 = RegisteredClassesAdapter(todayList, context!!, requireActivity(), parentFragmentManager)
                        binding.todayRecycler.adapter = adapter2

                    }catch (e :NullPointerException){
                        println( e.localizedMessage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context!!, error.message, Toast.LENGTH_LONG).show()
            }
        })


        binding.helloUserTv.text = "Hello ${user.displayName},\nReady to learn?"


        binding.findTutorsBtn.setOnClickListener {
            val move = ProfilePageFragmentDirections.
            actionProfilePageFragmentToFindTutorsFrag(imgUri.toString())
            findNavController().navigate(move)
        }
        binding.userPp.setOnClickListener {
            getPermission()
            galleryIntent()
        }

        binding.logOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(Html.fromHtml("<font color='#7A5252'>Yes</font>", 0))
                { _, _ ->
                    auth.signOut()
                    val move = ProfilePageFragmentDirections
                        .actionProfilePageFragmentToSignInFragment()
                    findNavController().navigate(move)
                }
                .setNegativeButton(Html.fromHtml("<font color='#7A5252'>No</font>", 0))
                {dialog,_->
                    dialog.dismiss()
                }
                .show()


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

    private fun retrieveAndSavePic(){
        picPhoto.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })

    }

    private fun displayPic(){
        println("image uri = ${user.photoUrl}")
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES)
            == PackageManager.PERMISSION_GRANTED)
            println("permission granted")
        Picasso.with(context).load(user.photoUrl).placeholder(R.drawable.user).into(binding.userPp)
    }

    private fun galleryIntent() {
        retrieveAndSavePic()
        displayPic()
    }

}