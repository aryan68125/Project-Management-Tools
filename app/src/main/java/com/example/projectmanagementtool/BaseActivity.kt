package com.example.projectmanagementtool

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat.postDelayed
import com.example.projectmanagementtool.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

//This activity will hold several functions that we will be using in many different activities
//This BaseActivity will be inherited by all other activities in the application
open class BaseActivity : AppCompatActivity() {

    //Tracking the back button functionality and when the user back button twice then the app will be closed
    private var doublePressToExitPressedOnce = false

    //We want to display the progress dialog each time something is loading
    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    //This function will show the progress dialog
    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        //set the screen content from a layout resource
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).setText(text)
        //Start the Dialog and display it on the screen
        mProgressDialog.show()
    }

    //FIREBASE GET USER ID
    //This function will hide the progress bar
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    //This function will get the current user ID
    fun getCurrentUserID() :String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    //this function will close the application if the user pressed the back button twice
    fun doubleBackToExit()
    {
        //handles the second click and closes the app upon detection of the second click
        if(doublePressToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doublePressToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_again_to_close_app),
            Toast.LENGTH_SHORT
        ).show()

        //add a little delay so that if the user press the back button twice after a long duration of time intervals then the application won't be closed
        Handler().postDelayed({
            doublePressToExitPressedOnce = false
        },1000)
    }

    //display errors in a snackbar
    fun showErrorSnackBar(message : String){
        //create a SnackBar here
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        //define how the snackBar is gonna look
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        //display snackBar
        snackBar.show()
    }
}
