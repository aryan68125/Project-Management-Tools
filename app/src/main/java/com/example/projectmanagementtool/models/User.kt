package com.example.projectmanagementtool.models

import android.os.Parcel
import android.os.Parcelable

/*
This class holds as bunch of information about the user
make this data class parcelable

What is a Parcelable class:-> Interface for classes whose instances can be written to and restored from a Parcel.
                              Classes implementing the Parcelable interface must also have a non-null static field
                              called CREATOR of a type that implements the Parcelable.Creator interface.
There are two ways of making the class parcelable either you write down all the code yourself or You can use a plugin
The plugin that we used here is Android Parcelable code generator
you can install this by going into settings -> PlugIns ->  Android Parcelable code generator
 */
data class User (
        val id: String = "", //This will hold the unique ID of the user
        val name: String = "", //This will hold the name of the user
        val email: String= "", //This till hold the email of the user
        val image: String = "", //this hold the directory OR Path of the user profile image
        val mobile:Long=0, //this will handle the mobile number of the user
        val fcmToken:String="", //This will allow us to know that the specific user has logged in
        var selected : Boolean = false
        ) : Parcelable {

        constructor(parcel : Parcel) : this(
                //all the Strings here is nullable hence we need to add !! at the end
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readLong(),
                parcel.readString()!!
        )

        //assign 0 to describe the parcel
        override fun describeContents() = 0

        // Strings to parcel
        //dest here is destination
        override fun writeToParcel(dest: Parcel, p1: Int) = with(dest) {
                //write Strings for all the values we have
                writeString(id)
                writeString(name)
                writeString(email)
                writeString(image)
                writeLong(mobile)
                writeString(fcmToken)
        }

        companion object {
                @JvmField
                val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
                        override fun createFromParcel(source: Parcel): User = User(source)
                        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
                }
        }
}
