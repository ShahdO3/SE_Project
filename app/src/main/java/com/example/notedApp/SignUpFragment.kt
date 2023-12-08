package com.example.notedApp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.notedApp.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var progress:BeautifulProgressDialog
    lateinit var binding: FragmentSignUpBinding
    lateinit var googleSignInC: GoogleSignInClient
    var imgUri: Uri? = null
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private var chosePhoto:Boolean = false
    private var picPhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imgUri = result.data!!.data!!
            println("here got uri = $imgUri")
            requireActivity().contentResolver.takePersistableUriPermission(
                imgUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
            displayPic()
        }else
            println("result code is n")

    }
    private fun getPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
//                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 0)
        }
        else if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            println("permission denied for MANAGE_EXTERNAL_STORAGE")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        progress = BeautifulProgressDialog(activity,
            BeautifulProgressDialog.withLottie, "Please wait...")
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)
        progress.setLayoutColor(Color.WHITE)

        databaseRef = FirebaseDatabase
            .getInstance()
            .getReference("instructors")

        val googleSignIO = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInC = GoogleSignIn.getClient(requireActivity(), googleSignIO)

        binding.signinWGoogle.setOnClickListener {
            progress.show()
            signInGoogle()
        }

        val mList = mutableListOf<TutorsData>()

        binding.uploadImageBtn.setOnClickListener {
            getPermission()
            galleryIntent()}
        binding.uploadImage.setOnClickListener  {
            getPermission()
            galleryIntent()}

        var emailCorrect = false
        var passCorrect = false
        var passMatching = false


        firebaseAuth = FirebaseAuth.getInstance()
        binding.welcome.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.fade_in_up))

        /*
        CHECKING EMAIL AND PASS'S ARE CORRECT WHILE USER IS TYPING THEM
         */
        binding.emailEt.addTextChangedListener {
            binding.emailEt.hint = ""
            if (!Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches())
                binding.emailTIL.helperText = "Invalid Email!"
            else {
                binding.emailTIL.helperText = null
                emailCorrect = true
            }
        }
        binding.passEt.addTextChangedListener {
            if (binding.passEt.text.toString().length < 8)
                binding.passTIL.helperText = "Minimum 8 Character Password!"
            else {
                binding.passTIL.helperText = null
                passCorrect = true
            }
        }
        binding.passEtReEnter.addTextChangedListener {
            if (binding.passEt.text.toString() != binding.passEtReEnter.text.toString())
                binding.rePassTIL.helperText = "Passwords Not Matching!"
            else {
                binding.rePassTIL.helperText = null
                passMatching = true
            }
        }

        binding.alreadyHaveAccTV.setOnClickListener {
            val move = SignUpFragmentDirections.
                actionSignUpFragment2ToSignInFragment2()
            findNavController().navigate(move)
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (emailCorrect && passCorrect && passMatching && chosePhoto){
                progress.show()
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful){
                        progress.dismiss()
                        val move = SignUpFragmentDirections.
                        actionSignUpFragment2ToSignUpSuccessFrag(imgUri.toString())
                        findNavController().navigate(move)
                    }else{
                        progress.dismiss()
                        Toast.makeText(context,
                            " Error was : ${it.exception?.localizedMessage}",
                            Toast.LENGTH_LONG).show()
                    }
                }

            }else {
                if (email.isBlank())
                    binding.emailEt.error = "Must Enter an Email!"
                if (pass.isBlank())
                    binding.passTIL.helperText = "Must Enter a Password"
                if (!chosePhoto)
                    Toast.makeText(context,
                        "Must choose a photo to proceed",
                        Toast.LENGTH_LONG).show()

            }


        }

        return binding.root
    }


    private fun signInGoogle() {
        val signingIntent = googleSignInC.signInIntent
        googleIntent.launch(signingIntent)
    }
    private var googleIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if (it.resultCode == Activity.RESULT_OK){
            GoogleSignIn.getSignedInAccountFromIntent(it.data).addOnCompleteListener {task->
                if (task.isSuccessful){
                    val account: GoogleSignInAccount? = task.result
                    if (account != null){
                        println("acc is NOT null")
                        val cred = GoogleAuthProvider.getCredential(account.idToken, null)
                        firebaseAuth.signInWithCredential(cred).addOnCompleteListener {signTask->
                            if (signTask.isSuccessful){

                                progress.dismiss()
                                println("sign in successful but idk whats up")
                                Toast.makeText(requireContext(), "Signed Up Successfully!",
                                    Toast.LENGTH_LONG).show()
                                val move = SignUpFragmentDirections.
                                actionSignUpFragment2ToSignUpSuccessFrag(imgUri.toString())
                                findNavController().navigate(move)
                            }else {
                                progress.dismiss()
                                Toast.makeText(
                                    requireContext(), signTask.exception!!.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }else {
                        progress.dismiss()
                        println("acc is null")
                        Toast.makeText(
                            requireContext(), "Account is null",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }else {
                    progress.dismiss()
                    println("idk couldn't get account")
                    Toast.makeText(
                        requireContext(), task.exception!!.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        println("res code not ok")
    }

    private fun retrieveAndSavePic(){
        picPhoto.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })


    }
    private fun displayPic(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES)
            == PackageManager.PERMISSION_GRANTED)
            println("permission granted")
        chosePhoto = true
        binding.uploadImage.visibility = View.VISIBLE
        binding.uploadImageBtn.visibility = View.INVISIBLE
        binding.uploadImage.setImageURI(imgUri)
    }
    private fun galleryIntent(){
        retrieveAndSavePic()
        displayPic()
    }




}