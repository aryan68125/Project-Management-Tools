package com.example.projectmanagementtool.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.utils.Constants

class TaskListActivity : BaseActivity() {

    var boardDocumentID = ""

    lateinit var Toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        //setting up the toolbar on SignUpActivity
        Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_task_list_activity)
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

        //getting the document Id from constants
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        //Now use the FireStoreClass() to get the board details by using document ID
        FireStoreClass().getBoardDetails(this,boardDocumentID)
    }

    fun boardDetails(board : Board){
       //hideProgressDialog()
        //set the toolbar title to the board name
       try
       {
           Toolbar
           var tv_title_task_list_activity =
               Toolbar.findViewById<TextView>(R.id.tv_title_task_list_activity)
           tv_title_task_list_activity.text = board.name
       }
       catch (e: Exception) {
           // handler
       }
    }
}