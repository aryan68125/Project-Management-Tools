package com.example.projectmanagementtool.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.BaseActivity
import com.example.projectmanagementtool.FireBase.FireStoreClass
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.adapters.CardMemberListItemsAdapter
import com.example.projectmanagementtool.dialogs.LabelColorListDialog
import com.example.projectmanagementtool.dialogs.MembersListDialog
import com.example.projectmanagementtool.models.*
import com.example.projectmanagementtool.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var Toolbar : Toolbar
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList : ArrayList<User>
    private var mSelectedDueDateMilliSeconds : Long = 0

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

        var tv_select_members = findViewById<TextView>(R.id.tv_select_members)
        tv_select_members.setOnClickListener {
            memberListDialog()
        }

        setupSelectedMembersList()

        //date picker dialog
        var tv_select_due_date = findViewById<TextView>(R.id.tv_select_due_date)
        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate
        if(mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            tv_select_due_date.text = selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDataPicker()
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
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )
        // Here we have assigned the update card details to the task list using the card position.
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
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

    //FUNCTIONS RELATED TO ADD MEMBERS LIST DIALOG
    //add members to the card that needs to finish the job
    fun memberListDialog()
    {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size>0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList)
                {
                    if(mMembersDetailList[i].id==j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }
        else{
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }

        //display the dialog box via an dialog box object
        val listDialog = object : MembersListDialog(
            this, mMembersDetailList, resources.getString(R.string.select_members)
        ){
            //implement functions here for the dialog box
            override fun onItemSelected(user: User, action: String) {


                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)
                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id==user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                //setupSelectedMembersList()
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    //this function will setup the selected members on to the card itself
    private fun setupSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices){
            for(j in cardAssignedMemberList)
            {
                if(mMembersDetailList[i].id==j){
                    //create a new object of SelectedMembers
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            var tv_select_members = findViewById<TextView>(R.id.tv_select_members)
            tv_select_members.visibility = View.GONE
            var rv_selected_members_list = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv_selected_members_list.visibility = View.VISIBLE
            //setup a layout manager for the recyclerView
            rv_selected_members_list.layoutManager = GridLayoutManager(this, 6)

            //setup an adapter
            val adapter = CardMemberListItemsAdapter(this,selectedMembersList , true)
            rv_selected_members_list.adapter = adapter

            //set an onclickListener to that adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        memberListDialog()
                    }
                }
            )
        }
        else{
            var tv_select_members = findViewById<TextView>(R.id.tv_select_members)
            tv_select_members.visibility = View.VISIBLE
            var rv_selected_members_list = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv_selected_members_list.visibility = View.GONE
        }
    }
    //....................END.............................

    //this function will handle the date picker
    /**
     * The function to show the DatePicker Dialog and select the due date.
     */
    private fun showDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                /*
                  The listener used to indicate the user has finished selecting a date.
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                var tv_select_due_date = findViewById<TextView>(R.id.tv_select_due_date)
                tv_select_due_date.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                /** Here we have get the time in milliSeconds from Date object
                 */

                /** Here we have get the time in milliSeconds from Date object
                 */
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }



}