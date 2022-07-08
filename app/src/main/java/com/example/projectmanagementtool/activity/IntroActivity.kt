package com.example.projectmanagementtool.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.signup_signin.SignInActivity
import com.example.projectmanagementtool.signup_signin.SignUpActivity

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

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

        //setting up the signUp Button here
        val SignUp_btn = findViewById<Button>(R.id.btn_sign_up)
        SignUp_btn?.setOnClickListener()
        {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            //adding custom activity transition animation
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        //setting up the SignIn button here
        val SignIn_btn = findViewById<Button>(R.id.btn_sign_in)
        SignIn_btn.setOnClickListener()
        {
            val intent2 = Intent(this,SignInActivity::class.java)
            startActivity(intent2)
            //adding custom activity transition animation
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    //Override the onBackPressed method to close the drawer
    override fun onBackPressed() {
            //this function is in BaseActivity so you need to inherit the baseActivity in your mainActivity
            doubleBackToExit()
    }
}