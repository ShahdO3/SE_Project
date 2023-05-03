package com.example.thelingo_projectshahdosman

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

private lateinit var context:Context
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        context = applicationContext
        supportActionBar?.hide()
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
}