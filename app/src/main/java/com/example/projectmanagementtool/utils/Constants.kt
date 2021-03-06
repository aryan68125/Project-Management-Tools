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
    //..........END.........................................

    //constants related to the collection of boards
    const val BOARDS : String = "boards"

    //keys in the firebase database related to board details
    const val ASSIGNED_TO : String = "assignedTo"
    const val DOCUMENT_ID : String = "documentId"

    //constants related to task list
    const val TASK_LIST : String = "taskList"

    //constants for our member activity
    const val BOARD_DETAIL: String = "board_detail"

    //constants to hold the user ID
    const val ID : String = "id"

    //constant to hold email address of the members
    const val EMAIL : String = "email"

    //constant to hold the key
    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"

    //constants for adding memebers in the cardview details activity
    const val BOARD_MEMBERS_LIST : String = "board_members_list"
    const val SELECT : String = "Select"
    const val UN_SELECT : String = "UnSelect"

    //this constant is for the application token for notification functionality
    const val TASKMASTER_PREFERENCES = "task_master_preferences"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "BH4ipwMfYObXvbZOIrVjFUdx6HRK7h2EvWoeiZ-9VgVFasegg2skKfkHZjqZPI5506QVhCWVzCmBBE-dUdzynsY"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
}