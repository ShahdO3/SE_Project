package com.example.thelingo_projectshahdosman

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.thelingo_projectshahdosman.databinding.FragmentSignInBinding
import com.example.thelingo_projectshahdosman.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    lateinit var googleSignInC: GoogleSignInClient
    var imgUri: Uri? = null
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var progress:BeautifulProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var emailCorrect = false
        var passCorrect = false
        progress = BeautifulProgressDialog(activity,
            BeautifulProgressDialog.withLottie, "Please wait...")
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)
        progress.setLayoutColor(Color.WHITE)

        binding = FragmentSignInBinding.inflate(inflater, container, false)
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

        binding.dontHaveAccTV.setOnClickListener {
            val move = SignInFragmentDirections.
                actionSignInFragmentToSignUpFragment()
            Navigation.findNavController(binding.root).navigate(move)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.forgotPass.setOnClickListener {
            if (emailCorrect) {
                val email = binding.emailEt.text.toString()
                AlertDialog.Builder(context)
                    .setTitle("Reset Password")
                    .setIcon(R.drawable.lock)
                    .setMessage("Are you sure you want to reset the password for the account" +
                            " with email $email?")
                    .setPositiveButton("Yes"){ dialog, _->
                        progress.setMessage("Sending email...")
                        progress.show()
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(context, "Email Sent, check your inbox!",
                                    Toast.LENGTH_LONG).show()
                            }else
                                Toast.makeText(context, it.exception!!.localizedMessage,
                                    Toast.LENGTH_LONG).show()
                            progress.dismiss()
                        }
                    }
                    .setNegativeButton("Cancel"){dialog, _->
                        dialog.dismiss()
                    }
                    .show()
            }else
                Toast.makeText(context, "Must Enter Email First!", Toast.LENGTH_LONG).show()
        }
        binding.signinBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            if (emailCorrect && passCorrect){
                progress.show()
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful){
                        progress.dismiss()
                        val move = SignInFragmentDirections.
                        actionSignInFragmentToProfilePageFragment()
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
            GoogleSignIn.getSignedInAccountFromIntent(it.data).addOnCompleteListener { task->
                if (task.isSuccessful){
                    val account: GoogleSignInAccount? = task.result
                    if (account != null){
                        println("acc is NOT null")
                        val cred = GoogleAuthProvider.getCredential(account.idToken, null)
                        firebaseAuth.signInWithCredential(cred).addOnCompleteListener {signTask->
                            if (signTask.isSuccessful){
                                progress.dismiss()
                                println("sign in successful but idk whats up")
                                val move = SignInFragmentDirections.
                                actionSignInFragmentToProfilePageFragment()
                                findNavController().navigate(move)

                                Toast.makeText(requireContext(), "Signed In Successfully, Welcome Back!",
                                    Toast.LENGTH_LONG).show()
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


}