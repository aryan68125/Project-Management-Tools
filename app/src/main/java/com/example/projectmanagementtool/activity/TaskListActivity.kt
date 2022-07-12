package com.example.projectmanagementtool.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.adapters.TaskListItemsAdapter
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.models.Card
import com.example.projectmanagementtool.models.Task
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDocumentID : String

    lateinit var Toolbar : Toolbar

    private lateinit var mBoardDetails : Board

    public lateinit var mAssignedMemberDetailList : ArrayList<User>

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
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        //Now use the FireStoreClass() to get the board details by using document ID
        FireStoreClass().getBoardDetails(this@TaskListActivity,mBoardDocumentID)
    }

    fun boardDetails(board : Board){

        mBoardDetails = board

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
        //show progress dialog
        //may fail
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    //This function adds or updates the task list
    fun addUpdateTaskListSuccess(){
        hideProgressDialog() //hide progress dialog that was successful to load the activity

        //get the board details
        FireStoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    //this function will create a new task in the board
    fun createTaskList(taskListName:String){
        val task = Task(taskListName, FireStoreClass().getCurrentUserID())
        //adding the task in the board's taskList ArrayList
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        //call the FireStoreClass to update the task list with the changes made to the board
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //This function will update taskList in the baord
    fun updateTaskList(position : Int, listName : String, model : Task){
        //get the task in the board
        val task = Task(listName, model.createdBy)
        //mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[position] = task
        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        //call the FireStoreClass to update the task list with the changes made to the board
        //this function not only update the taskList but also updates it
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //this function will delete the TaskList
    fun deleteTaskList(position : Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        //call the FireStoreClass to update the task list with the changes made to the board
        //this function not only update the taskList but also updates it
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //FUNCTIONS RELATED TO CARD LIST
    //This function will create a card in a TaskList inside the board
    fun addCardToTaskList(position : Int, cardName : String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUserList : ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreClass().getCurrentUserID())

        val card = Card(cardName,FireStoreClass().getCurrentUserID(), cardAssignedUserList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task

        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        //call the FireStoreClass to update the task list with the changes made to the board
        //this function not only update the taskList but also updates it
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun cardDetails(taskListPosition : Int, cardPosition : Int){
        var intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION , taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION , cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailsList(list : ArrayList<User>){
         mAssignedMemberDetailList = list
        //may fail
        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        //get the recyclerVew
        var rv_task_list = findViewById<RecyclerView>(R.id.rv_task_list)
        rv_task_list.layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this@TaskListActivity,mBoardDetails.taskList)
        rv_task_list.adapter = adapter
    }

    //this function will help us arrange the cards
    fun updateCardsInTaskList(taskListPosition : Int, cards : ArrayList<Card>){
       mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
       mBoardDetails.taskList[taskListPosition].cards = cards
        // can fail
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    //.................END.....................

    //FUNCTION TO HANDLE THE MENU IN THE TASK LIST ACTIVITY
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate the menu that I prepared
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                //start members activity
                var intent = Intent(this,MembersActivity::class.java)
                //pass on the detail
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && resultCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){
            //Now use the FireStoreClass() to get the board details by using document ID
            FireStoreClass().getBoardDetails(this,mBoardDocumentID)
        }
        else{
            Log.e("cancelled","cancelled")
        }
    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE : Int = 14
    }
    //........END.................
}