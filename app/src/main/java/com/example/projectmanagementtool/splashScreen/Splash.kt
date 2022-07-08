package com.example.projectmanagementtool.splashScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.activity.IntroActivity
import com.example.projectmanagementtool.MainActivity
import com.example.projectmanagementtool.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView: View = window.decorView
            // Hide the status bar.
            val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.setSystemUiVisibility(uiOptions)
        }

        //this code hides actionbar in an activity
        getSupportActionBar()?.hide();

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            //check if the currentUserID is null or not
            var currentUserID = FireStoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()) //if we have a user ID
            {
                val intent2 = Intent(this,MainActivity::class.java)
                startActivity(intent2)
                //adding custom activity transition animation
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                finish() //user will not be able to come back to this splash activity if use finish()
            }
            else{  //If we don't have a user ID
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                //adding custom activity transition animation
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                finish() //user will not be able to come back to this splash activity if use finish()
            }
        }, 4500) // 3000 is the delayed time in milliseconds.
    }
}