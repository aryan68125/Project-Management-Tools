package com.example.projectmanagementtool.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.projectmanagementtool.activity.MyProfileActivity

object Constants{
    //the whole document in the firebase database that holds the collection of data related to users and their accounts
    //for example profile picture , phone number, etc...
    const val USERS : String = "users"

    //keys in the firebase database related to user details
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val MOBILE : String = "mobile"

    //COMMON CODE RELATED TO CHOOSE AND IMAGE FROM THE GALLERY AND SETTING UP THAT TO THE IMAGEVIEW
    //create the constants for the permission code in the manifest.xml file
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    //this function will open up the gallery from where we choose our image to upload it as a profile picture
    //this function will also get the activity results
    fun showImageChooser(activity : Activity){
        //This will open up the gallery
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    //STORE IMAGE IN FIREBASE STORAGE BUCKET
    //function to understand the file extension of the file that we have downloaded based on the uri
    fun getFileExtension(activity : Activity, uri : Uri?) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
    //..........END.......................................

    //constants related to the collection of boards
    const val BOARDS : String = "boards"

    //keys in the firebase database related to board details
    const val ASSIGNED_TO : String = "assignedTo"
    const val DOCUMENT_ID : String = "documentId"
}