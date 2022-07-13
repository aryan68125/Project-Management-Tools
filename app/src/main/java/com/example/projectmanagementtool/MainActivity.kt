package com.example.projectmanagementtool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.activity.*
import com.example.projectmanagementtool.adapters.BoardItemsAdapter
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.messaging.FirebaseMessagingService;

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    //UPDATE THE USER PROFILE IMAGE IN THE NAVIGATION DRAWER HEADER
    //create the companion objects that will hold the information about our profile request code
    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11
        const val CREATE_BOARD_REQUEST_CODE : Int = 12
    }

    //create a variable for token that will be used to send and reciev notifications in the application
    private lateinit var mSharedPreferences: SharedPreferences

    //a variable to store user name
    private lateinit var mUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting up the toolbar on SignUpActivity
        val Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_main_activity)
        setSupportActionBar(Toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //setting up the Back button on the toolbar of SignUpActivity
        Toolbar.setNavigationIcon(resources.getDrawable(com.example.projectmanagementtool.R.drawable.ic_action_navigation_menu))
        Toolbar.setNavigationOnClickListener(View.OnClickListener {
            //Toggle the activity main drawer
            toggleDrawer()
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

        //setup the nav_View in the activity_main xml file
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this);

        //initializing the shared preferences here
        //MODE_PRIVATE tells the application that the shared preferences should only be available inside the application
        mSharedPreferences = this.getSharedPreferences(Constants.TASKMASTER_PREFERENCES, Context.MODE_PRIVATE)
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            //call the signInUser function in the FireStoreClass()
            FireStoreClass().loadUserData(this,true)
        }
        else {
            FirebaseMessaging.getInstance()
                .token
                .addOnSuccessListener(this@MainActivity) { instanceIdResult ->
                    updateFCMToken(instanceIdResult)
                }
        }

        //setup the floating action button
        //open up the create board activity when the button is clicked
        val app_bar_main = findViewById<CoordinatorLayout>(R.id.app_bar_main)
        val fab_create_board_app_bar_main = app_bar_main.findViewById<FloatingActionButton>(R.id.fab_create_board_app_bar_main)
        fab_create_board_app_bar_main.setOnClickListener(){
            var intent3 = Intent(this@MainActivity, CreateBoardActivity :: class.java)
            /*
            by the time this setOnclick listener is called "FireStoreClass().loadUserData(this)" code would have already bean executed
            which in turns calls "loadUserData(this)" in this activity from FireStoreClass() and in fireStoreClass()
            "activity.updateNavigationUserDetails(loggedInUser)" code is there which in turns calls "updateNavigationUserDetails(loggedInUser)" in the
            MainActivity which has the code "mUserName = user.name" so we already would have the user name by the time control of kotlin
            would reach this OnClickListener function set on the floating action button in the mainActivity UI xml
             */
            //send the currently logged in username to createBoardActivity
            intent3.putExtra(Constants.NAME,mUserName)
            //this will update the UI when a new board is created in the recylerView of the main Activity
            startActivityForResult(intent3,CREATE_BOARD_REQUEST_CODE)
            Log.e("floating button","Button pressed")
        }
}//on create function

    //get the user details from the firebase and update the profile picture in the navigation drawer
    //User here is the projectmanagement user and not the firebase user
    fun updateNavigationUserDetails(user : User, readBoardsList : Boolean)
    {
        hideProgressDialog()
        //getting the user object and extracting username from it and storing it in the mUserName variable
        mUserName = user.name

        //bringing in the nav_view from the activity main xml
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //getting the imageView that shows the profile picture from the navigation drawer header xml named nav_header_main
        val imageView_nav_header = navigationView.findViewById<ImageView>(R.id.iv_nav_user_image);

        //load the image from the user
        //set the loaded image to the imageView inside the nav_header_main xml
        Glide
            .with(this) //select the component where you want to use ir
            .load(user.image)  //image url of the user
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)  //set the placeholder for the imageView
            .into(imageView_nav_header) //select the imageView where you want to see the result of the above operations

        //Now set the Text that will show the user name
        //get the textView from the navigation drawer header xml named nav_header_main
        val usernameTextView = navigationView.findViewById<TextView>(R.id.tv_username)
        usernameTextView.text = user.name

        //Load the Boards List
        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this)
        }
    }

    //CODE RELATED TO UI LOGIC IN THE MAIN ACTIVITY
    //This function is used to Toggle the drawer
    private fun toggleDrawer(){
        /*
        getting the id drawer_layout from the activity main
         */
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) //If the drawer is open
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else //If the drawer is closed
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    //Override the onBackPressed method to close the drawer
    override fun onBackPressed() {
        /*
        Check if the drawer layout is open
         */
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) //If the drawer is open
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else //If the drawer is closed
        {
            //this function is in BaseActivity so you need to inherit the baseActivity in your mainActivity
            doubleBackToExit()
        }
    }

    /*
         setting up the Menu Options in the main activity drawer
         This function will only work if you have inherited from NavigationView.OnNavigationItemSelectedListener class
         This function will handle the menu item selection
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                //OpenUp the Intro Activity
                val intent = Intent(this, MyProfileActivity::class.java)
                //add flags to the intent
                /*
                Function of these flags
                flag -> FLAG_ACTIVITY_CLEAR_TOP
                It will prevent the intent from opening a new activity if that particular activity is already running on the background
                Basically helps prevents multiple copies of activities running on the background
                flag -> FLAG_ACTIVITY_NEW_TASK
                If the activity is already running on the background it will bring that activity back on the foreground and reload the
                state it was in prior to going into background

                here start the activity for result to update the profile image in the navigation drawer
                 */
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                //get the MY_PROFILE_REQUEST_CODE from MyProfileActivity
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
                //adding custom activity transition animation
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
            }
            R.id.nav_sign_out ->{
                //SignOut the current user from the fireBase account
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                //OpenUp the Intro Activity
                val intent = Intent(this, IntroActivity::class.java)
                //add flags to the intent
                /*
                Function of these flags
                flag -> FLAG_ACTIVITY_CLEAR_TOP
                It will prevent the intent from opening a new activity if that particular activity is already running on the background
                Basically helps prevents multiple copies of activities running on the background
                flag -> FLAG_ACTIVITY_NEW_TASK
                If the activity is already running on the background it will bring that activity back on the foreground and reload the
                state it was in prior to going into background
                 */
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //adding custom activity transition animation
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                finish() //prevent user from comming back to MainActivity by pressing the back button after signing out
            }
            R.id.nav_developer_info->{
                //OpenUp the Dev Activity
                val intent = Intent(this, DeveloperActivity::class.java)
                //add flags to the intent
                /*
                Function of these flags
                flag -> FLAG_ACTIVITY_CLEAR_TOP
                It will prevent the intent from opening a new activity if that particular activity is already running on the background
                Basically helps prevents multiple copies of activities running on the background
                flag -> FLAG_ACTIVITY_NEW_TASK
                If the activity is already running on the background it will bring that activity back on the foreground and reload the
                state it was in prior to going into background
                 */
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //adding custom activity transition animation
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
            }
        }
        //close the drawer after the operation is complete
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    //This function will set the MY_PROFILE_REQUEST_CODE in this activity when it gets MY_PROFILE_REQUEST_CODE from the MyProfileActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check if MY_PROFILE_REQUEST_CODE was the RESULT_OK sent from the MyProfileActivity
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE)
        {
            //we need to load the user data in this activity
            FireStoreClass().loadUserData(this)
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            //this will update the UI when a new board is created in the recylerView of the main Activity
            FireStoreClass().getBoardsList(this)
        }
        else{ //in case there was an error
            Log.e("Canelled","cancelled")
        }
    }
    //............END..............................

    //CODE RELATED TO THE RECYCLER VIEW AND IT'S ADAPTER
    //function to populate our recyclerview It will only handle the UI It's not gonna download the data from the firebase database
    fun populateBoardsListToUI(boardsList : ArrayList<Board>){
        hideProgressDialog()

        //check if the boards list is empty or not
        if(boardsList.size > 0) //if the boardsList is not empty
        {
            //make the recyclerView visible in the UI
            val rv_boards_list = findViewById<RecyclerView>(R.id.rv_boards_list)
            rv_boards_list.visibility = View.VISIBLE
            //make the tv_no_boards_available textView invisible
            val tv_no_boards_available = findViewById<TextView>(R.id.tv_no_boards_available)
            tv_no_boards_available.visibility = View.GONE

            //tell the rv_boards_list RecyclerView what layoutManager to use here we are using a Linear Layout
            rv_boards_list.layoutManager = LinearLayoutManager(this)
            //tell rv_boards_list RecyclerView to have a fixed size
            rv_boards_list.setHasFixedSize(true)

            //initialize the adpater that we created earlier for our RecylerView
            val adapter = BoardItemsAdapter (this,boardsList)
            //assign that adapter to the RecyclerView rv_boards_list
            rv_boards_list.adapter = adapter

            //set onclick Listener to the recyclerView of the main activity
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    var intent  = Intent(this@MainActivity,TaskListActivity::class.java)
                    //sending the documentID to constants
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        }
        else{ //if the boardsList is empty
            //make the recyclerView Invisible in the UI
            val rv_boards_list = findViewById<RecyclerView>(R.id.rv_boards_list)
            rv_boards_list.visibility = View.GONE
            //make the tv_no_boards_available textView visible
            val tv_no_boards_available = findViewById<TextView>(R.id.tv_no_boards_available)
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }
    //...........END.............................

    //FUNCTION RELATED TO APPLICATION TOKEN FOR THE NOTIFICATION FUNCTIONALITY IS HERE
    fun tokenUpdateSuccess(){
        hideProgressDialog()

        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this,true)
    }

    //this function will update the token in the database
    fun updateFCMToken(token : String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }
    //........................END.....................................................
}
