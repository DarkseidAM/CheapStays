package com.cg.cheapstays.presenter.user.settings

import com.cg.cheapstays.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsPresenter(val view : View) {
    companion object{
        lateinit var fDatabase: FirebaseDatabase
        lateinit var fAuth:FirebaseAuth
    }

    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
    }

    // Get user details from firebase
    fun getUserFireBase(){
        val id = fAuth.currentUser?.uid!!
        val ref =  fDatabase.reference.child("users").child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue(Users::class.java)!!
                    if(user.userType=="employee")
                    {
                        val hotelid = snapshot.child("hotelId").value.toString()
                        view.getUserStatus(user.name,user.email,user.phone,hotelid)
                    }
                    else{
                        view.getUserStatus(user.name,user.email,user.phone,null)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Updating user's details
    fun updateUserFireBase(name: String, phone: String) {
        val id = fAuth.currentUser?.uid!!
        val ref =  fDatabase.reference.child("users").child(id)
        if(!name.isNullOrEmpty())
            ref.child("name").setValue(name)
        if(!phone.isNullOrEmpty())
            ref.child("phone").setValue(phone)
    }

    fun signOutUser(){
        fAuth.signOut()
    }
    interface View {
        fun getUserStatus(name: String, email: String, phone: String, hotelid: String?)
    }
}