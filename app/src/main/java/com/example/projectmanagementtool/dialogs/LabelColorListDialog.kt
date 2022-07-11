package com.example.projectmanagementtool.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagementtool.R
import com.example.projectmanagementtool.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog (context : Context, private var list : ArrayList<String>, private val title : String = "", private val mSelectedColor : String = "") : Dialog(context){

    //create an object of the adapter for the dialog box
    private var adapter : LabelColorListItemsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    //this function will setup the recyclerView of the colors
    private fun setUpRecyclerView(view : View){
        //design the view
        var tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = title
        var rvList = view.findViewById<RecyclerView>(R.id.rvList)
        rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        rvList.adapter = adapter

        adapter!!.onItemClickListener = object : LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                 dismiss()
                onItemSelected(color)
            }

        }
    }
    protected abstract fun onItemSelected(color : String)
}