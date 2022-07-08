package com.example.projectmanagementtool.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    //IMAGE TO BE UPLOADED
    //This variable rill hold the image uri or location in the storage of the device
    private var mselectedImageFileUri: Uri? = null

    //variable to store the downloadable image url
    private var mProfileImageURL : String = ""

    //IMAGE UPLOAD VARIABLES HERE
    //this variable is initialized in setUserDataUI function of this activity
    private lateinit var mUserDetails : User
    private lateinit var mUserID: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        //setting up the toolbar
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_my_profile_activity)
        setSupportActionBar(Toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //setting up the Back button on the toolbar of SignUpActivity
        Toolbar.setNavigationIcon(resources.getDrawable(com.example.projectmanagementtool.R.drawable.ic_white_back))
        Toolbar.setNavigationOnClickListener(View.OnClickListener {
            //go back
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

        //LOGIC TO POPULATE THE EDITTEXT FIELDS AND IMAGE CHOOSER TO SELECT THE PROFILE PICTURE
        //call the firebase function
        FireStoreClass().loadUserData(this)

        var iv_user_image_my_profile_activity = findViewById<ImageView>(R.id.iv_user_image_my_profile_activity)
        iv_user_image_my_profile_activity.setOnClickListener(){
            //check if the user have given the permission to access the external storage or not
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                   //If permission is granted then show the image chooser
                   Constants.showImageChooser(this)
            }
            else{
                //if permission is not granted then ask for permission from the user
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE )
            }
        }

        var btn_update_my_profile_activity = findViewById<Button>(R.id.btn_update_my_profile_activity)
        btn_update_my_profile_activity.setOnClickListener(){
            if(mselectedImageFileUri !=null)
            {
                uploadUserImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    //catch permission results via this function
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) //If the user has granted the read permission code then
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //SHOW OUR IMAGE CHOOSER HERE
                Constants.showImageChooser(this)
            }
            else{
                Toast.makeText(this,"Permission denied by the user",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //This function get activated only after Activity Results are fetched
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check if the result codes are ok
        if (resultCode == Activity.RESULT_OK && data!!.data !=null)
        {
            mselectedImageFileUri = data.data //saving the image Uri in a variable

            try{
                //load the image from the gallery
                //set the loaded image to the imageView inside the nav_header_main xml
                val iv_user_image_my_profile_activity = findViewById<ImageView>(R.id.iv_user_image_my_profile_activity)
                Glide
                    .with(this@MyProfileActivity) //select the component where you want to use ir
                    .load(Uri.parse(mselectedImageFileUri.toString()))  //image url of the user but here the image will be loaded from the device so instead of url uri will be used
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)  //set the placeholder for the imageView
                    .into(iv_user_image_my_profile_activity) //select the imageView where you want to see the result of the above operations
            }
            catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    //This function will set the user information in the UI of My Profile activity
    //user here is from our application models
    fun setUserDataInUI(user : User){
        //get the user from the FireStore class and set it to mUserDetails
        mUserDetails = user
        mUserID = user

        //find the image view by id in the UI xml file of this MyProfileActivity
        val iv_user_image_my_profile_activity = findViewById<ImageView>(R.id.iv_user_image_my_profile_activity)

        //load the image from the user
        //set the loaded image to the imageView inside the nav_header_main xml
        Glide
            .with(this) //select the component where you want to use ir
            .load(user.image)  //image url of the user from the firebase
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)  //set the placeholder for the imageView
            .into(iv_user_image_my_profile_activity) //select the imageView where you want to see the result of the above operations

        //set the name of the user with the name of the logged in user
        val et_name_my_profile = findViewById<EditText>(R.id.et_name_my_profile)
        et_name_my_profile.setText(user.name)
        val et_email_my_profile_activity = findViewById<EditText>(R.id.et_email_my_profile_activity)
        et_email_my_profile_activity.setText(user.email)
        val et_mobile_my_profile_activity = findViewById<EditText>(R.id.et_mobile_my_profile_activity)
        if(user.mobile!=0L)
        {
            et_mobile_my_profile_activity.setText(user.mobile.toString())
        }
    }
    //END...........................................................................................

    //Upload the image to the Firebase storage bucket
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mselectedImageFileUri != null)
        {
            //before we can store the image we need a storage reference
            /*
            By keeping storage reference equal to user's uid we can save some space in the firebase storage bucket as the
            previously uploaded profile picture of the user will be replaced by the new one instead of uploading a new picture every time
            that will just cost you extra storage if we allow to do that
             */
            val sRef : StorageReference = FirebaseStorage.getInstance().reference
                .child("USER_IMAGE"+ mUserID.id + "."+ Constants.getFileExtension(this,mselectedImageFileUri))

            //then store the image in the firebase storage bucket
            sRef.putFile(mselectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                /*
                taskSnapshot contains image download url and we need that image download url
                to store image in the firebase database
                 */
                Log.e("Firebase image url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    /*
                    uri here is the actual link of the image
                    store this uri in a variable
                     */
                    Log.e("Downloadable image url", uri.toString())
                    mProfileImageURL =  uri.toString()

                    //UPDATE the user profile data
                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    //this function will load the image from the firebase storage bucket in to this application
    //This function will be called each time our profile update is successful
    fun profileUpdateSuccess(){
        hideProgressDialog()
        //send the MY_PROFILE_REQUEST_CODE to the main activity so that we can update the user profile picture in the navigation drawer header imageview
        setResult(Activity.RESULT_OK)

        //closing the current activity and preventing user from getting back to this activity by pressing back button
        //it will send the user back to the mainactivity or previous activity after his/her profile has been successfully updated
        finish()
    }

    /*
    Override the finish function to add a custom animation when the MyProfile activity closes
    after the updation of user profile data is complete and goes back to the mainActivity
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(com.example.projectmanagementtool.R.anim.slide_in_right, com.example.projectmanagementtool.R.anim.slide_out_left)
    }

    //This function will update the user profile data
    private fun updateUserProfileData(){

        var anyChangesMade = false

        //create a user hashmap
        val userHashMap = HashMap<String,Any>()
        /*
        add the values to the user HashMap from the application MyProfileActivity
        Name of the user, user's mobile number and user's profile picture URL will be added to this hashmap
         */
        //SETTING UP THE IMAGE IN THE HASHMAP
        //check if the mProfileImageUrl is not empty and mProfileImageUrl is not the same as the previously uploaded image url
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image)
        {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade = true
        }

        //SETTING UP THE USER NAME TO THE HASHMAP
        //getting the user name from the EditText of the UI xml file of MyProfileActivity
        var et_name_my_profile = findViewById<EditText>(R.id.et_name_my_profile)
        var username_editText = et_name_my_profile.text.toString()
        if(username_editText != mUserDetails.name)
        {
            userHashMap[Constants.NAME] = username_editText
            anyChangesMade = true
        }

        //SETTING UP THE USER MOBILE NUMBER TO THE HASHMAP
        //getting the user mobile number from the EditText of the UI xml file of MyProfileActivity
        var et_mobile_my_profile_activity = findViewById<EditText>(R.id.et_mobile_my_profile_activity)
        var mobile_number_edit_text = et_mobile_my_profile_activity.text.toString()
        if(mobile_number_edit_text != mUserDetails.mobile.toString())
        {
            userHashMap[Constants.MOBILE] = mobile_number_edit_text.toLong()
            anyChangesMade = true
        }

        //Now pushing all the changes to the firebase database
        //Call the updateUserProfileData() function in the FireStoreClass() that we created
        if(anyChangesMade == true)
        {
            FireStoreClass().updateUserProfileData(this, userHashMap)
            hideProgressDialog()
        }
    }

    //END......................................

    //this function will animate the dev activity when doing sliding out animation when back button is pressed
    override fun onBackPressed() {
        super.onBackPressed()
        this@MyProfileActivity.overridePendingTransition(com.example.projectmanagementtool.R.anim.slide_in_right, com.example.projectmanagementtool.R.anim.slide_out_left)
    }
}