package com.example.projectmanagementtool.adapters

import android.R
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanagementtool.models.Board


/*
Recycler View needs an adapter so this Adapter class will handle the recycler View
This BoardItemsAdapter inherits from recylerView Adapter class

here our class BoardItemsAdapter gets a list of boards
 */
class BoardItemsAdapter(private val context : Context, private val list : ArrayList<Board>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //create an instance or an object of onclickListener
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(com.example.projectmanagementtool.R.layout.item_board,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //use the model so a single board should be our model since the class is getting a list of boards
        val model = list[position]

        //check if the user is MyViewHolder
        if(holder is MyViewHolder)
        {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(com.example.projectmanagementtool.R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById(com.example.projectmanagementtool.R.id.iv_board_image))

            //set the text in the texView
            holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_name).text = model.name
            holder.itemView.findViewById<TextView>(com.example.projectmanagementtool.R.id.tv_created_by).text = "Created By : ${model.createdBy}"

            //set the onclick Listener
            holder.itemView.setOnClickListener{
                //assign an onclick event here
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    //create an interface for onClickListener
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    //create a function for onclickListener For the Recyclerview items
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
       return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
    }
}