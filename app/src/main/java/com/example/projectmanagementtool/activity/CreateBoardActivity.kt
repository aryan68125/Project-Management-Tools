package com.example.projectmanagementtool.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    //variables related to boardImage URL
    //variable to store the image URL that we get from the fireStore bucket
    private var mBoardImageURL : String = ""
    //variable to store the image url of the selected image from the gallery
    private var mselectedImageFileUri : Uri? = null

    //this variable will store the name of currently logged in user
    //which we get from MainActivity
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        //if the intent has something for the Constant.NAME then get the user name from MainActivity and set it to mUserName variable
        /*
        Here we are reusing the username that we have already got when we first make a connection to the firebase database and this
        code below in the if statement allow us get the username in a single database request from our application to the firebase database
        saving us from making another request to the firebase database for getting the username
         */
        if(intent.hasExtra(Constants.NAME)){
            //get the user name from MainActivity and set it to mUserName variable
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        //setting up the toolbar on SignUpActivity
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_create_board_activity)
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

        var iv_board_image = findViewById<ImageView>(R.id.iv_board_image)
        iv_board_image.setOnClickListener(){
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

        //add an onClickListener to Create button
        var btn_create_board_create_board_activity = findViewById<Button>(R.id.btn_create_board_create_board_activity)
        btn_create_board_create_board_activity.setOnClickListener {
            if(mselectedImageFileUri != null){
                uploadBoardImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    //FUNCTIONS RELATED TO CREATION OF BOARDS
    //This function will create a board in the firebase database
    private fun createBoard(){
        // for now assign the currently logged in user to this board
        //This arrayList holds the people that is assigned to the board
        val assignedUsersArrayList : ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        //create a board object that will be later sent to the firebase database
        var et_board_name_create_board_activity = findViewById<EditText>(R.id.et_board_name_create_board_activity)
        var board = Board(
                    //here we will have to get the board name from the EditText in the ui of CreateBoard Activity
                    et_board_name_create_board_activity.text.toString()  ,    //name of the board
                    mBoardImageURL,                                           //Url of the board image
                    mUserName,                                                //name of the user who created the board in this case it will be currently logged in user
                    assignedUsersArrayList                                    //list of people assigned to this board
        )

        /*
        Create the board in the firebase Database
        calling the "createBoard()" from the "FireStoreClass()" that we created
        this createBoard() function in the fireStoreClass does all the real Firebase
        related stuff like creation of the board collection and storing all the information onto it
         */
        FireStoreClass().createBoard(this,board)
    }

    //function to upload the board image to the database and to the fireStore bucket
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mselectedImageFileUri != null)
        {
            //before we can store the image we need a storage reference
            /*
            By keeping storage reference equal to user's uid we can save some space in the firebase storage bucket as the
            previously uploaded profile picture of the user will be replaced by the new one instead of uploading a new picture every time
            that will just cost you extra storage if we allow to do that
             */
            //HERE I WANT THE IMAGE UPLOADED BY THE USER TO HAVE DOCUMENT ID
            val sRef : StorageReference = FirebaseStorage.getInstance().reference
                .child("Board_IMAGE"+ System.currentTimeMillis() + "."+ Constants.getFileExtension(this,mselectedImageFileUri))

            //then store the image in the firebase storage bucket
            sRef.putFile(mselectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                /*
                taskSnapshot contains image download url and we need that image download url
                to store image in the firebase database
                 */
                Log.e("Board image url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    /*
                    uri here is the actual link of the image
                    store this uri in a variable
                     */
                    Log.e("Downloadable image url", uri.toString())
                    mBoardImageURL =  uri.toString()

                    //CREATE the user Board collection
                    createBoard()
                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    //this function will let us know if the board is created successfully
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        //this will update the UI when a new board is created in the recylerView of the main Activity
        setResult(Activity.RESULT_OK)
        finish()
    }
    //..............END.......................

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
                Toast.makeText(this,"Permission denied by the user", Toast.LENGTH_SHORT).show()
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
                //set the loaded image to the imageView inside the activity_create_boar xml
                var iv_board_image = findViewById<ImageView>(R.id.iv_board_image)
                Glide
                    .with(this) //select the component where you want to use ir
                    .load(Uri.parse(mselectedImageFileUri.toString()))  //image url of the user but here the image will be loaded from the device so instead of url uri will be used
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)  //set the placeholder for the imageView
                    .into(iv_board_image) //select the imageView where you want to see the result of the above operations
            }
            catch(e: IOException){
                e.printStackTrace()
            }
        }
    }
}