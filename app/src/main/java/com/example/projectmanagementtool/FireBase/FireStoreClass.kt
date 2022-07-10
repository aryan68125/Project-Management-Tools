package com.example.projectmanagementtool.FireBase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmanagementtool.MainActivity
import com.example.projectmanagementtool.activity.CreateBoardActivity
import com.example.projectmanagementtool.activity.MembersActivity
import com.example.projectmanagementtool.activity.MyProfileActivity
import com.example.projectmanagementtool.activity.TaskListActivity
import com.example.projectmanagementtool.models.Board
import com.example.projectmanagementtool.models.User
import com.example.projectmanagementtool.signup_signin.SignInActivity
import com.example.projectmanagementtool.signup_signin.SignUpActivity
import com.example.projectmanagementtool.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

/*
This class contains all the relevant code related to firebase store database
 */
class FireStoreClass {

    //create an instance of the firebase fire store
    private val mFireStore = FirebaseFirestore.getInstance()

    //FUNCTIONS RELATED TO USER
    //create a new user OR Register a new user in the database
    /*
    Here the idea is that we not only need to register the user in the authentication module but also in the database
    for example if you want to add a profile picture of the user in the database then this function will help you to
    achieve that

    NOTE -> here import user from models and not the user that we get from the fireStore

    here in this function we are passing the signUpActivity as a parameter hence we can use that to access all the functions there are in the SignUp activity
    the user is the user that we just created in the signUp activity
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        //create a new collection in the FireStore database
        /*
        create a document for every single user that we have because we want to store details for every single user that we have
        for that we are gonna be requiring the current userId
        This will create a document for the current user in the application
         */
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }.addOnFailureListener { e ->
            Log.e(activity.javaClass.simpleName, "Error writing document")
        }

        //Here in this function register the user
    }

    //This function will sign in the user and load it's data
    /*
    pass in the SignInActivity because we want to use some functions in that activity

    Modify the SinInUSer function so that we can download the user profile picture from the Firebase
     */
    fun loadUserData(activity: Activity, readBoardsList : Boolean = false) {
        //get the user's information from the fire store database
        mFireStore.collection(Constants.USERS).document(getCurrentUserID()).get()
            .addOnSuccessListener { document ->

                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                //store the logged in user ID in the application
                //our document in the firestore database have this information about the logged in User
                /*
            By using toObject() I want to make a user object of whatever is given tome in the fireStore database
             */
                val loggedInUser =
                    document.toObject(User::class.java) //This is going to give us the logged in user

                //Now I can Sign in that user
                //this signInSuccess function is in the SignInActivity
                if (loggedInUser != null) {
                    //check if an activity is of certain type
                    when (activity) {
                        is SignInActivity -> { //the activity is a SignInActivity
                            //function is to sign in the user to it's account
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity -> { // the activity is MainActivity
                            //function is to load data from the firebase about the current logged in user and update profile picture in the navigation drawer
                            activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                        }
                        is MyProfileActivity -> {  //the activity is MyProfileActivity
                            //function is to repopulate the user details in MyProfileActivity
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener { e ->
            //check if an activity is of certain type
            when (activity) {
                is SignInActivity -> { //the activity is a SignInActivity
                    activity.hideProgressDialog()
                }
                is MainActivity -> {
                    //run the function updateNavigationUserDetails in the main activity
                    activity.hideProgressDialog()
                }
            }
        }
    }

    /*
    This function will update the user profile data in the firebase when the user updates his/her profile in the MyProfile Activity
    HAshmap here can have a String as a key and a value of type Any
    Any is nothing but an object in java . In kotlin we call objects Any when passing it in the hashMap
     */
    fun updateUserProfileData(activity : MyProfileActivity, userHashMap : HashMap<String,Any>){
        //this function will pass in the user as a hashmap
        //get the firestore collection and update the data present in that collection
        mFireStore.collection(Constants.USERS).document(getCurrentUserID()).update(userHashMap).addOnSuccessListener {
            //here we are going to update the collection in the firebase database
            Log.i(activity.javaClass.simpleName, "Profile data is updated in the firebase collection")
            Toast.makeText(activity,"Profile updated!!",Toast.LENGTH_SHORT).show()

            //call the profileUpdateSuccess() from MyProfileActivity
            activity.profileUpdateSuccess()
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while updating")
            Toast.makeText(activity,"Profile update Failed!",Toast.LENGTH_SHORT).show()
        }
    }

    //This function will just give us the current user Id
    /*
      Check if there is an instance of the current user
      if the current user is null then send the user to the IntroActivity
      if the current user is not null then send the user to the main activity
     */
    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        //check if the current user is null or not
        if (currentUser != null) {
            /*
        this currentUserID is gonna be used to download the correct information regarding the current user
        such as what board does he have , what is his/her profile picture , his phone number etc...
         */
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    //.................END.......................

    //FUNCTIONS RELATED TO BOARD CRUD FUNCTIONALITY
    //this function will create the board in the firebase database
    fun createBoard(activity : CreateBoardActivity, board : Board){
        //create a board collection with a random id and set the board into the board collection in the firebase database
       mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge()).addOnSuccessListener {
           Toast.makeText(activity,"Board created!",Toast.LENGTH_SHORT).show()

           //run the function "boardCreatedSuccessfully()" in the CreateBoardActivity
           activity.boardCreatedSuccessfully()
       }.addOnFailureListener {
           e->
           activity.hideProgressDialog()
           Log.e("Create Board","Error while creating board")
           Toast.makeText(activity,"Board creation Failed!",Toast.LENGTH_SHORT).show()
       }
    }

    //function to get the data about the boards list from the firebase database
    fun getBoardsList(activity : MainActivity){
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID()).get().addOnSuccessListener {
            document ->
            //create a boards List
            val boardList : ArrayList<Board> = ArrayList()
            /*
            This document is a snapshot so it will only show the board that is assigned to us
             */
            for(i in document.documents){
                //converting the documents object from firebase to board object
                val board = i.toObject(Board::class.java)!!
                //use that board object to get the id of the board
                board.documentId = i.id
                boardList.add(board)
            }
            //populate the activity ui in the main activity Recyclerview
            activity.populateBoardsListToUI(boardList)
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
        }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener {
                document ->

            //get the board details
            val board = document.toObject(Board::class.java)!!
            //now assign the document id to the board
            board.documentId = document.id

            //get Board details here do that in the taskList Activity
            activity.boardDetails(board)
        }.addOnFailureListener{
                e->
            activity.hideProgressDialog()
        }
    }

    //function to add or update tasklist to the firebase database
    fun addUpdateTaskList(activity : TaskListActivity, board : Board){
        //a task list as an array of task
        //create a hashmap or a dictionary of the tasklist because we want to add date created and etc...
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        //create an entry in the database of the firebase
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"TaskList updated successfully")

            //call the function addUpdateTaskListSuccess() in the TaskListActivity class
            activity.addUpdateTaskListSuccess()
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while updating TaskList",e)
        }
    }
    //.........................END........................

    //FUNCTIONS RELATED TO ASSIGNED MEMBERS TO THE BOARD
    fun getAssignedMembersListDetails(activity : MembersActivity, assignedTo: ArrayList<String>){
         mFireStore.collection(Constants.USERS).whereIn(Constants.ID,assignedTo).get().addOnSuccessListener {
             document ->
             Log.e(activity.javaClass.simpleName, document.documents.toString())

             val usersList : ArrayList<User> = ArrayList()
             for(i in document.documents){
                 val user = i.toObject(User::class.java)!!
                 usersList.add(user)
             }
             activity.setUpMembersList(usersList)
         }.addOnFailureListener {
             e->
             activity.hideProgressDialog()
             Log.e(activity.javaClass.simpleName, "Error while creating a board",e)
         }
    }

    //this function will get the member details
    fun getMemberDetails(activity : MembersActivity, email : String){
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL, email).get().addOnSuccessListener {
            document ->
            if(document.documents.size > 0){
                val user = document.documents[0].toObject(User::class.java)!!
                activity.memberDetails(user)
            }
            else{
                activity.hideProgressDialog()
                activity.showErrorSnackBar("No such Member found!")
            }
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while adding a member to the board",e)
        }
    }

    //this function will assign new member to the board
    fun assignMemberToBoard(activity : MembersActivity, board : Board, user : User){
        //make an hashmap of the assignedTo
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashMap).addOnSuccessListener {
            activity.membersAssignSuccess(user)

        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while adding a member to the board",e)
        }
    }
    //....END...........................................
}