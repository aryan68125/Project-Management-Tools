package com.example.projectmanagementtool.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.projectmanagementtool.R

class DeveloperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        //setting up the toolbar on SignUpActivity
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_developer_activity)
        setSupportActionBar(Toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //setting up the Back button on the toolbar of SignUpActivity
        Toolbar.setNavigationIcon(resources.getDrawable(com.example.projectmanagementtool.R.drawable.ic_white_back))
        Toolbar.setNavigationOnClickListener(View.OnClickListener {
            //Go back to IntroActivity
            onBackPressed()
        })

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

        //setting up the Display Message
        val message = """
            Hello there I am Aditya Kumar. Thank you for downloading my application.
            Feel free to visit my website DevConnect and check out my other projects.
            here is my link:- https://radiant-bastion-62859.herokuapp.com/profile/3947f970-07f0-4863-bdf6-0d247c0b2e82/
            """.trimIndent()
        var textView_info = findViewById<TextView>(R.id.textView_info)
        textView_info.setText(message)
    }

    //this function will animate the dev activity when doing sliding out animation when back button is pressed
    override fun onBackPressed() {
        super.onBackPressed()
        this@DeveloperActivity.overridePendingTransition(com.example.projectmanagementtool.R.anim.slide_in_right, com.example.projectmanagementtool.R.anim.slide_out_left)
    }
}