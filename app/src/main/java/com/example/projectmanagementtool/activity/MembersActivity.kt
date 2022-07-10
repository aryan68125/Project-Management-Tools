package com.example.projectmanagementtool.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.adapters.MemberListItemsAdapter
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.utils.Constants

class MembersActivity : BaseActivity() {
    lateinit var Toolbar : Toolbar
    lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>

    //this flag will tell us if there are any changes in the activity
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        //setting up the toolbar on SignUpActivity
        Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_activity_members)
        setSupportActionBar(Toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //setting up the Back button on the toolbar of SignUpActivity
        Toolbar.setNavigationIcon(resources.getDrawable(com.example.projectmanagementtool.R.drawable.ic_back_member_activity))
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

        //catch the intent passed on from the TaskList activity
        if(intent.hasExtra(Constants.BOARD_DETAIL))
        {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
        }
    }
    //this function will setup the recyclerView adapter and update the UI if any changes to the members list happen
    fun setUpMembersList(list : ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()

        var rv_members_list = findViewById<RecyclerView>(R.id.rv_members_list)
        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        //setting up the adapter to the RecyclerView
        val adapter = MemberListItemsAdapter(this,list)
        rv_members_list.adapter = adapter
    }

    //function to get the member details
    fun memberDetails(user: User){
        //assign the users to mBoardDetails
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this , mBoardDetails , user)
    }

    //this function will handle the member assigned to the board functionality
    fun membersAssignSuccess(user : User){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true

        setUpMembersList(mAssignedMembersList)
    }

    //FUNCTION TO HANDLE THE MENU IN THE MEMBERS ACTIVITY
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate the menu that I prepared
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                //open a dialog box to add members
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //this function will handle the dialog box custom
    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            //get the email form the AppCompatEditText et_email_search_member
            var et_email_search_member = dialog.findViewById<AppCompatEditText>(R.id.et_email_search_member)
            var email = et_email_search_member.text.toString()
            if (email.isNotEmpty())
            {
                dialog.dismiss()
                //add the logic to add the member to a board
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)
            }
            else
            {
                Toast.makeText(this,"Please enter email address",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    //........END.................

    override fun onBackPressed() {
        if(anyChangesMade)
        {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
}