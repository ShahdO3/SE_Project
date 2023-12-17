package com.example.notedApp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.example.notedApp.databinding.ActivityHomeBinding
import com.example.notedApp.databinding.FragmentProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

private lateinit var context:Context
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var imgUri : Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var instructorsRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var imageStorageRef: StorageReference
    lateinit var progress: BeautifulProgressDialog
    lateinit var userCPP:CircleImageView
    private var picPhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            progress.show()
            imgUri = result.data!!.data!!
            println("here got uri = $imgUri")
            this.contentResolver.takePersistableUriPermission(imgUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //compressing image before putting in Firebase Storage for faster upload time
            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(this
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
                                val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(img)
                                    .build()
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful)
                                            Picasso.with(context).load(imgUri).placeholder(R.drawable.user).into(userCPP)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ugh", "in home activity or creating it")
        binding = ActivityHomeBinding.inflate(layoutInflater)
        Log.d("ugh", binding.root.toString())
        setContentView(binding.root)

        context = applicationContext
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        imgUri = user.photoUrl!!

        progress = BeautifulProgressDialog(this,
            BeautifulProgressDialog.withLottie, "Please wait...")
        progress.setLottieLocation("progressLottieAesthetic.json")
        progress.setLottieLoop(true)
        progress.setLayoutColor(Color.WHITE)

//        Getting a reference to the current user
        userRef = FirebaseDatabase
            .getInstance()
            .getReference("users")
            .child(user.uid)
        imageStorageRef = FirebaseStorage.getInstance()
            .getReference("profileImg")
            .child(user.uid)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

//  This part is to show the search bar
        val searchItem = menu?.findItem(R.id.search_action)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.queryHint = "What are you looking for?"
        searchView.setOnQueryTextListener(object :androidx.appcompat.widget.
        SearchView.OnQueryTextListener, SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//               Filter the results from all the pages
                return false
            }

        })

        val userPPItem = menu.findItem(R.id.userPPic)
        val view = MenuItemCompat.getActionView(userPPItem)
        userCPP = view.findViewById(R.id.user_pp_topbar)

        Picasso.with(context).load(imgUri).placeholder(R.drawable.user).into(userCPP)

        userCPP.setOnClickListener {
            getPermission()
            galleryIntent()
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.logout_action->{
                val builder :AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Are you sure?")
                    builder.setMessage("Are you sure you want to log out?")
                    builder.setPositiveButton(Html.fromHtml("<font color='#7A5252'>Yes</font>", 0))
                    { _, _ ->
                        auth.signOut()
//                        val newIntent = Intent(this, StartActivity::class.java)
//                        startActivity(newIntent)
                        val move = ProfilePageFragmentDirections.actionProfilePageFragmentToStartActivity()
                        findNavController(R.id.fragmentContainerView).navigate(move)
                    }
                    .setNegativeButton(Html.fromHtml("<font color='#7A5252'>No</font>", 0))
                    {dialog,_->
                        dialog.dismiss()
                    }
                    builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{

        fun dayToNum(day:String):Int{
            when(day.lowercase()){
                "sun"->return 1
                "mon"->return 2
                "tue"->return 3
                "wed"->return 4
                "thu"->return 5
                "fri"->return 6
                "sat"->return 7
            }
            return -1
        }

        fun hourTo12Hour(hr:Int):Int{
            return if (hr > 12)
                hr-12
            else
                hr
        }

        fun hourTo24Hour(hr:Int):Int{
            return if (hr < 12)
                hr+12
            else
                hr
        }

    }

    /*
    code gotten from:
    https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    to automatically remove focus from editText when pressing elsewhere
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
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
        if (ContextCompat.checkSelfPermission(
                com.example.notedApp.context,
                Manifest.permission.READ_MEDIA_IMAGES)
            == PackageManager.PERMISSION_GRANTED)
            println("permission granted")
        Picasso.with(context).load(user.photoUrl).placeholder(R.drawable.user).into(userCPP)
    }

    private fun galleryIntent() {
        retrieveAndSavePic()
        displayPic()
    }

    private fun getPermission(){
        if (ContextCompat.checkSelfPermission(
                com.example.notedApp.context,
                Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 0)
        }
        else if (ContextCompat.checkSelfPermission(
                com.example.notedApp.context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            println("permission denied for MANAGE_EXTERNAL_STORAGE")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }

    }
}