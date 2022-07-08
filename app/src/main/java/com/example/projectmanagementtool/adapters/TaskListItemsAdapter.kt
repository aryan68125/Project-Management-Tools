package com.example.projectmanagementtool.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.models.Task

open class TaskListItemsAdapter(private val context: Context, private var list : ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task,parent,false)
        //set the linear layout as the layout parameters
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins(
            (15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0
        )
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if (holder is MyViewHolder) {

                if (position == list.size - 1) { //if there is no task in the list then show add task list button
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.ll_task_item).setVisibility(View.GONE);
                }
                else {  //if there is a task in the list then hide the add task button
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_add_task_list).setVisibility(View.GONE)
                    holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.ll_task_item).setVisibility(View.VISIBLE)
                }
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

    /**
     * This view  describes some item view and get the data about their place within the view
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}