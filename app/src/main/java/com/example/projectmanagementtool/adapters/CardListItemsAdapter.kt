package com.example.projectmanagementtool.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.activity.TaskListActivity
import com.example.projectmanagementtool.models.Card
import com.example.projectmanagementtool.models.SelectedMembers


// TODO (Step 3: Create an adapter class for cards list.)
// START
open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false)
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        var rv_card_selected_members_list = holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)

        if(model.labelColor.isNotEmpty())
        {
            var view_label_color =  holder.itemView.findViewById<View>(R.id.view_label_color)
            view_label_color.visibility = View.VISIBLE
            view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
        }
        else{
            var view_label_color =  holder.itemView.findViewById<View>(R.id.view_label_color)
            view_label_color.visibility = View.GONE
        }

        holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

        // Now with use of public list of Assigned members detail List populate the recyclerView for Assigned Members.
        // START
        if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0) {
            // A instance of selected members list.
            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

            // Here we got the detail list of members and add it to the selected members list as required.
            for (i in context.mAssignedMemberDetailList.indices) {
                for (j in model.assignedTo) {
                    if (context.mAssignedMemberDetailList[i].id == j) {
                        val selectedMember = SelectedMembers(
                            context.mAssignedMemberDetailList[i].id,
                            context.mAssignedMemberDetailList[i].image
                        )

                        selectedMembersList.add(selectedMember)
                    }
                }
            }

            if (selectedMembersList.size > 0) {

                if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                    rv_card_selected_members_list.visibility = View.GONE
                } else {

                    rv_card_selected_members_list.visibility = View.VISIBLE

                    rv_card_selected_members_list.layoutManager = GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                    rv_card_selected_members_list.adapter = adapter
                    adapter.setOnClickListener(object :
                        CardMemberListItemsAdapter.OnClickListener {
                        override fun onClick() {
                            if (onClickListener != null) {
                                onClickListener!!.onClick(position)
                            }
                        }
                    })
                }
            } else {
                rv_card_selected_members_list.visibility = View.GONE
            }
        }

        if (holder is MyViewHolder) {
            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END