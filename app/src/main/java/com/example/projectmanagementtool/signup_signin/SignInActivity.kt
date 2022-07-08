package com.example.projectmanagementtool.signup_signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.MainActivity
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    //Firebase sign in function initialization
    //initializing firebase authentication auth variable
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //FIREBASE
        //get the current of firebase authentication class from the application
        auth = FirebaseAuth.getInstance()

        //setting up the toolbar on SignUpActivity
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_sign_in_activity)
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
            // Hide the status bar
            val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.setSystemUiVisibility(uiOptions)
        }

        //Initialize the SignIn button in the signIn activity
        val SignIn = findViewById<Button>(R.id.btn_sign_in_sign_in_activity)
        SignIn.setOnClickListener(){
            //call the signRegisteredUser() function here
            signRegisteredUser()
        }
    }

    //function that will sign in the users
    private fun signRegisteredUser(){

        //FIREBASE SIGN IN FUNCTION Logic
        //get the email and password so that we can sign in the user
        val email : String = findViewById<EditText>(R.id.et_email_sign_in).text.toString().trim{
            it<= ' '
        }
        val password : String = findViewById<EditText>(R.id.et_password_sign_in).text.toString().trim{
            it<= ' '
        }
        if(validateForm(email,password))
        {
            // Show progress dialog
            //Show the Progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            //Here perform authentication and sign in the user
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    //hide the progress dialog
                    hideProgressDialog()

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signIn success", "signInWithEmail:success")
                        // TODO (Step 2: Remove the toast message and call the FirestoreClass signInUser function to get the data of user from database. And also move the code of hiding Progress Dialog and Launching MainActivity to Success function.)
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FireStoreClass().loadUserData(this@SignInActivity)
                        // END
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signIn failed", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //FIREBASE VALIDATION
    //validate the form
    private fun validateForm(email : String, password : String) : Boolean{
        //check if that we have got from the fields is not empty
        return when {
            TextUtils.isEmpty(email)-> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }

    //This function will be used to signIn the user and retrieve all it's information from the firebase firestore database
    fun signInSuccess(user: User){
        //hide the progress dialog
        hideProgressDialog()

        //then start an activity which is going to send us to the main activity
        //FOR TESTING
        //open up the main activity
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        //prevent user from getting back to signin  activity by pressing the back button
        finish()
    }

    //this function will animate the dev activity when doing sliding out animation when back button is pressed
    override fun onBackPressed() {
        super.onBackPressed()
        this@SignInActivity.overridePendingTransition(com.example.projectmanagementtool.R.anim.slide_in_right, com.example.projectmanagementtool.R.anim.slide_out_left)
    }
}