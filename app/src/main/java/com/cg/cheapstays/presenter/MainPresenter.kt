package com.cg.cheapstays.presenter

import com.cg.cheapstays.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainPresenter(val view :View) {

    companion object{
        lateinit var fDatabase: FirebaseDatabase
        lateinit var fAuth: FirebaseAuth
    }

    //INITIALIZE FIREBASE
    fun initialize(){
        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()
    }

    //CHECK IF USER EXISTS AND GET TYPE
    fun checkUserFireBase(){
        val id = fAuth.currentUser?.uid!!
        val ref =  fDatabase.reference.child("users").child(id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val userType = snapshot.getValue(Users::class.java)?.userType
                    view.checkUserStatus("Success", userType)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                view.checkUserStatus(error.message, null)
            }
        })
    }


    interface View {
        fun checkUserStatus(msg: String, userType: String?)
    }
}