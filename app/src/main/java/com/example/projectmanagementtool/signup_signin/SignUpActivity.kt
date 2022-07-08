package com.example.projectmanagementtool.signup_signin


import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

//This class inherits from BaseActivity
class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.projectmanagementtool.R.layout.activity_sign_up)

        //setting up the toolbar on SignUpActivity
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_sign_up_activity)
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

        //Handleing Signup Button here
        val Signup_btn = findViewById<Button>(R.id.btn_sign_up_sign_up_activity)
        Signup_btn.setOnClickListener(){
            registerUser()
        }
    }

    //FIREBASE REGISTRATION SIGNUP FEATURES
    //Funtion to register a user
    private fun registerUser(){
        //it <= ' ' trims the empty spaces at the end of the String
        val name : String = findViewById<EditText>(R.id.et_name).text.toString().trim{
            it<=' '
        }
        val email : String = findViewById<EditText>(R.id.et_email).text.toString().trim{
            it<=' '
        }
        val password : String = findViewById<EditText>(R.id.et_password).text.toString().trim{
            it<=' '
        }
        //validate the form using validateForm function
        if(validateForm(name,email,password))
        {
            //Signup the user here after validation

            //Show the Progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            //Firebase signUp
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                //hide the progress dialog box
                hideProgressDialog()

                //perform the signing up task
                if (task.isSuccessful) {   //If task was successful
                    //result and task are nullable here so use !!
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    //get the registered email
                    val registeredEmail = firebaseUser.email!!

                    //create a new user object
                    val user = User(firebaseUser.uid,name,registeredEmail)
                    //call the function registerUser from the FireStoreClass.kt and not the firestore from the firebase library
                    FireStoreClass().registerUser(this,user)
                } else  //If task was not successful
                {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //FIREBASE VALIDATION
    //validate the form
    private fun validateForm(name : String, email : String, password : String) : Boolean{
        //check if that we have got from the fields is not empty
        return when {
            TextUtils.isEmpty(name)-> {
                showErrorSnackBar("Please enter a name")
                false
            }
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

    //show a toast when the user is registered in the fireStore database after signUp process is complete
    //call this function in fireStore class
    fun userRegisteredSuccess(){
        //hide progress bar
        hideProgressDialog()

        Toast.makeText(this,"You have successfully registered", Toast.LENGTH_SHORT).show()

        //TESTING PURPOSES
        //Log the user out
        FirebaseAuth.getInstance().signOut()
        //adding custom animation
        onBackPressed()
        finish()
    }

    //this function will animate the dev activity when doing sliding out animation when back button is pressed
    override fun onBackPressed() {
        super.onBackPressed()
        this@SignUpActivity.overridePendingTransition(com.example.projectmanagementtool.R.anim.slide_in_right, com.example.projectmanagementtool.R.anim.slide_out_left)
    }
}