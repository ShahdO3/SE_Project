package com.example.notedApp


import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.notedApp.databinding.FragmentSignUpSuccessBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException


class SignUpSuccessFrag : DialogFragment() {
    private val args : SignUpSuccessFragArgs by navArgs()
    lateinit var binding: FragmentSignUpSuccessBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var databaseRef: DatabaseReference

    private lateinit var imgStorageRef: StorageReference


    lateinit var progress: BeautifulProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        progress = BeautifulProgressDialog(activity,
            BeautifulProgressDialog.withLottie, "Please wait...")
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
        imgStorageRef = FirebaseStorage.getInstance()
            .getReference("profileImg")
            .child(user.uid)

        binding = FragmentSignUpSuccessBinding.inflate(inflater, container, false)

        val name:EditText = binding.nameEt
        val submit:Button = binding.submit

        submit.setOnClickListener {

            if (name.text.isBlank())
                name.error = "Must Fill!"
            else{
                progress.show()
                databaseRef.child("name").setValue(name.text.toString())
                println(user.uid)
                val imageUri = Uri.parse(args.imageURL)
                //compressing image before putting in Firebase Storage for faster upload time
                var bmp: Bitmap? = null
                try {
                    bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()
                bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                val fileInBytes: ByteArray = baos.toByteArray()
                imgStorageRef.putBytes(fileInBytes).addOnCompleteListener{uploadTask->
                    if (uploadTask.isSuccessful){
                        uploadTask.result.storage
                            .downloadUrl.addOnCompleteListener {
                                progress.setMessage("Uploading image...")
                            if (it.isSuccessful){
                                val img = it.result
                                databaseRef.child("imageUri").setValue(img.toString())
                                val profileUpdates: UserProfileChangeRequest= UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.text.toString())
                                    .setPhotoUri(img)
                                    .build()
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener {
                                        progress.setMessage("Thank you for being so patient...")
                                        if (it.isSuccessful) {
                                            progress.dismiss()
                                            val move =
                                                SignUpSuccessFragDirections.actionSignUpSuccessFragToHomeActivity(
                                                    img.toString(),
                                                    name.text.toString()
                                                )
                                            findNavController().navigate(move)
                                            println("Saved the profileeee")
                                        }
                                        else
                                            println("not saved")
                                    }
                            }
                        }
                    }
                }


            }
        }

        return binding.root
    }


}