package com.cg.cheapstays.presenter

import com.cg.cheapstays.model.Users
import com.cg.cheapstays.view.USER_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpPresenter (val view : View) {
    companion object{
        lateinit var fDatabase: FirebaseDatabase
        lateinit var fAuth: FirebaseAuth
    }

    fun initialize(){
        // Initializing Firebase's Database and Auth Instances
        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
    }

    fun getHotelsFireBase(){
        // Getting the hotel list for the employee to select
        fDatabase.reference.child("hotels").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotels = mutableListOf<String>()
                val hotelId = mutableListOf<String>()
                if(snapshot.exists()){
                    for(childs in snapshot.children){
                        hotels.add(childs.child("name").value.toString())
                        hotelId.add(childs.key!!)
                    }
                    // Giving a callback to activity
                    view.setHotelAdapter(hotels,hotelId)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun signUpFireBase(name : String,email: String, password: String, hotelid:String?=null ) {
        // Signing Up with Email & Password
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                val users = Users(name,email, USER_TYPE,"")
                val id = it.result?.user?.uid
                fDatabase.reference.child("users").child(id!!).setValue(users)
                if(USER_TYPE=="employee")    fDatabase.reference.child("users").child(id).child("hotelId").setValue(hotelid)
                // Callback to activity
                view.signUpStatus("Success")
            }
            else{
                view.signUpStatus("${it.exception?.message}")
            }
        }
    }

    interface View {
        fun setHotelAdapter(hotels: MutableList<String>, hotelId: MutableList<String>)
        fun signUpStatus(msg: String)
    }
}