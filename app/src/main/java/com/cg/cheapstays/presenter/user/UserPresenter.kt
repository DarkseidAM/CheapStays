package com.cg.cheapstays.presenter.user

import com.cg.cheapstays.view.USER_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserPresenter (val view : View) {

    companion object{
        lateinit var fDatabase: FirebaseDatabase
        lateinit var fAuth: FirebaseAuth
    }


    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
    }

    // Getting the current logged in user
    fun setUserTypeFireBase(){
        fDatabase.reference.child("users").child(fAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                    USER_TYPE = snapshot.child("userType").value.toString()
                    view.userTypeStatus("SUCCESS")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    interface View {
        fun userTypeStatus(msg: String)
    }
}