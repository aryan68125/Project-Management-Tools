package com.example.projectmanagementtool.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.activity.TaskListActivity
import com.example.projectmanagementtool.models.Task

open class TaskListItemsAdapter(private val context: Context, private var list : ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dialogDeleteNote: androidx.appcompat.app.AlertDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(com.example.projectmanagementtool.R.layout.item_task, parent,false)
        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if (holder is MyViewHolder) {

                if (position == list.size - 1) { //if there is no task in the list then show add task list button
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById<LinearLayout>(com.example.projectmanagementtool.R.id.ll_task_item).setVisibility(View.GONE);
                }
                else {  //if there is a task in the list then hide the add task button
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.GONE)
                    holder.itemView.findViewById<LinearLayout>(com.example.projectmanagementtool.R.id.ll_task_item).setVisibility(View.VISIBLE)
                }

                //set the title of the task
                holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_task_list_title).text = model.title
                //set on click listener on the tv_add_task_list
                holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setOnClickListener{
                    //make add task list cardView visible
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.GONE)
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_add_task_list_name).setVisibility(View.VISIBLE)
                }

                //set on click listener on ib_close_list_name image button this is image button close
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_close_list_name).setOnClickListener{
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.VISIBLE)
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_add_task_list_name).setVisibility(View.GONE)
                }
                //set on click listener on ib_done_list_name image button this is image button close
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_done_list_name).setOnClickListener{
                    //create entry in the firebase database and display the task list
                    val listName = holder.itemView.findViewById<EditText>(com.example.projectmanagementtool.R.id.et_task_list_name).text.toString()

                    if(listName.isNotEmpty()){
                        if(context is TaskListActivity){
                            context.createTaskList(listName)
                        }
                    }
                    else{
                        //let the user know that he cannot create an empty board
                        Toast.makeText(context, "Please enter a Task List name",Toast.LENGTH_SHORT).show()
                    }
                }
                //set onclick Listener on ib_edit_list_name ImageButton
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_edit_list_name).setOnClickListener {
                    //set eh name of the list to the et_edit_task_list_name EditText
                      holder.itemView.findViewById<EditText>(com.example.projectmanagementtool.R.id.et_edit_task_list_name).setText(model.title)

                    //make the ll_title_view LinearLayout INVISIBLE
                    holder.itemView.findViewById<LinearLayout>(com.example.projectmanagementtool.R.id.ll_title_view).visibility = View.GONE
                    //make the cv_edit_task_list_name CardView VISIBLE
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_edit_task_list_name).visibility = View.VISIBLE
                }
                //set on click listener on ib_close_editable_view image button this is image button close
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_close_editable_view).setOnClickListener{
                    //make the ll_title_view LinearLayout INVISIBLE
                    holder.itemView.findViewById<LinearLayout>(com.example.projectmanagementtool.R.id.ll_title_view).visibility = View.VISIBLE
                    //make the cv_edit_task_list_name CardView VISIBLE
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_edit_task_list_name).visibility = View.GONE
                }
                //set on click listener on ib_done_edit_list_name ImageButton
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_done_edit_list_name).setOnClickListener{
                    //get the taskList name from the et_edit_task_list_name EditText
                    val listName = holder.itemView.findViewById<EditText>(com.example.projectmanagementtool.R.id.et_edit_task_list_name).text.toString()
                    if(listName.isNotEmpty()){
                        if(context is TaskListActivity){
                            context.updateTaskList(position, listName, model)
                        }
                    }
                    else{
                        //let the user know that he cannot create an empty board
                        Toast.makeText(context, "Please enter a Task List name",Toast.LENGTH_SHORT).show()
                    }
                }

                //set onclick Listener on ib_delete_list ImageButton
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_delete_list).setOnClickListener {
                    //delete the list here
                    alertDialogForDeleteList(position,model.title)
                }

                //set an onClickListener on tv_add_card TextView
                holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_card).setOnClickListener{
                    //make cv_add_card CardView visible
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_card).setVisibility(View.GONE)
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_add_card).setVisibility(View.VISIBLE)
                }
                //set onclick Listener on ib_close_card_name ImageButton
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_close_card_name).setOnClickListener {
                    //make tv_add_card TextView visible
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_card).setVisibility(View.VISIBLE)
                    holder.itemView.findViewById<CardView>(com.example.projectmanagementtool.R.id.cv_add_card).setVisibility(View.GONE)
                }
                //set onclick Listener on ib_done_card_name ImageButton
                holder.itemView.findViewById<ImageButton>(com.example.projectmanagementtool.R.id.ib_done_card_name).setOnClickListener {
                    //create entry in the firebase database and display the Card list
                    val cardName = holder.itemView.findViewById<EditText>(com.example.projectmanagementtool.R.id.et_card_name).text.toString()

                    if(cardName.isNotEmpty()){
                        if(context is TaskListActivity){
                            //call the function that create cards here
                            context.addCardToTaskList(position, cardName)
                        }
                    }
                    else{
                        //let the user know that he cannot create an empty board
                        Toast.makeText(context, "Please enter a Task Card name",Toast.LENGTH_SHORT).show()
                    }
                }

                //load the cards in the rv_card_list RecyclerView
                var rv_card_list = holder.itemView.findViewById<RecyclerView>(com.example.projectmanagementtool.R.id.rv_card_list)
                rv_card_list.layoutManager = LinearLayoutManager(context)
                rv_card_list.setHasFixedSize(true)
                val adapter = CardListItemsAdapter(context, model.cards)
                rv_card_list.adapter = adapter

                adapter.setOnClickListener(
                    object : CardListItemsAdapter.OnClickListener{
                        override fun onClick(cardPosition: Int) {
                            if(context is TaskListActivity){
                                context.cardDetails(position, cardPosition)
                            }
                        }
                    }
                )
                /////
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Here we are using only 70% of the entire screen available to us on a device for our recyclerView so we are using
       this function to actually calculate the dp or density pixels
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    // START
    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        //setting up a custom dialog box
        val builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        //set title for alert dialog
        builder.setTitle("Alert!")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        //builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
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
    // END


    /**
     * This view  describes some item view and get the data about their place within the view
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}