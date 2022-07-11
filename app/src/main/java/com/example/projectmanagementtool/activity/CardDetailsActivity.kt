package com.example.projectmanagementtool.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.dialogs.LabelColorListDialog
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.models.Card
import com.example.projectmanagementtool.models.Task
import com.example.projectmanagementtool.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var Toolbar : Toolbar
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        //get the info from the intent from TaskListActivity
        getIntentData()

        //setting up the toolbar on SignUpActivity
        Toolbar = findViewById<Toolbar>(com.example.projectmanagementtool.R.id.toolbar_activity_card_details)
        setSupportActionBar(Toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //setting up the Back button on the toolbar of SignUpActivity
        Toolbar.setNavigationIcon(resources.getDrawable(com.example.projectmanagementtool.R.drawable.ic_white_back))
        Toolbar.findViewById<TextView>(R.id.tv_title_activity_card_detail).setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
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

        var et_name_card_details = findViewById<AppCompatEditText>(R.id.et_name_card_details)
        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        //set on click listener on the update card button
        var btn_update_card_details = findViewById<Button>(R.id.btn_update_card_details)
        btn_update_card_details.setOnClickListener {
            if(et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            }
            else{
                Toast.makeText(this,"Enter a card name",Toast.LENGTH_SHORT).show()
            }
        }

        //set the onClickListener on the tv_select_label_color TextView
        var tv_select_label_color = findViewById<TextView>(R.id.tv_select_label_color)
        tv_select_label_color.setOnClickListener{
            labelColorsListDialog()
        }
    }

    //this function will add or update the card in the task list activity from cardDetailsActivity
    fun addUpdateTaskListSuccess(){
        //hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
        //create a card object
        var et_name_card_details = findViewById<AppCompatEditText>(R.id.et_name_card_details)
        val card = Card(
        et_name_card_details.text.toString(),
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )
        // Here we have assigned the update card details to the task list using the card position.
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        //create a new card item since we have the updated card
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //this function deletes a card
    private fun deleteCard()
    {
        val cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        //removing card
        cardList.removeAt(mCardPosition)

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        //updating the taskList also avoiding add Card button so that it doesn't get deleted accidently from the UI
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardList
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //create a function to get Intent data
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL))
        {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    //FUNCTION RELATED TO MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card->{
                //delete the card here
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //......END......

    //FUNTIONS RELATED TO DIALOG BOXES
    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteCard(cardName : String) {
        //setting up a custom dialog box
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        //set title for alert dialog
        builder.setTitle("Alert!")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $cardName.")
        //builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to add some static label colors in the list.
     */
    private fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }
    private fun setColor(){
        var tv_select_label_color = findViewById<TextView>(R.id.tv_select_label_color)
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }
    private fun labelColorsListDialog()
    {
        val colorsList : ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this, colorsList, resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ){
            ///implement on item selected
            override fun onItemSelected(color: String) {
                //get the color
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }
//.....................END...........................
}