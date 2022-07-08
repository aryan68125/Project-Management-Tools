package com.example.projectmanagementtool.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class Board (
    val name: String = "",                              //name of the board
    val image : String = "",                            //image assigned to the board
    val createdBy : String = "",                        //name of the user who created the board
    val assignedTo : ArrayList<String> = ArrayList(),   //name of peoples to whom this board is assigned to
    var documentId: String = "",                        //Id of the board
    var taskList : ArrayList<Task> = ArrayList()        //I contains the task associated with the board
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}