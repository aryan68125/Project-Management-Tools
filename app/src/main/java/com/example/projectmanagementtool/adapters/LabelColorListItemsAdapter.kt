package com.example.projectmanagementtool.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.R
import java.util.*

class LabelColorListItemsAdapter (private val context: Context, private var list: ArrayList<String>, private val mSelectedColor: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate( R.layout.item_label_color,parent,false )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is MyViewHolder) {

            var view_main = holder.itemView.findViewById<View>(R.id.view_main)
            view_main.setBackgroundColor(Color.parseColor(item))

            var iv_selected_color = holder.itemView.findViewById<ImageView>(R.id.iv_selected_color)

            if (item == mSelectedColor) {
                iv_selected_color.visibility = View.VISIBLE
            } else {
                iv_selected_color.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {

                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {

        fun onClick(position: Int, color: String)
    }
}
// END