package com.cg.cheapstays.presenter.user.hotels

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelBasePresenter (val view : View) {

    companion object{
        lateinit var fDatabase: FirebaseDatabase
        lateinit var fAuth : FirebaseAuth
    }
    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
    }
    fun getHotelId(){
        val ref = fDatabase.reference.child("users").child(fAuth.currentUser?.uid!!)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view.sendId(snapshot.child("hotelId").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })
    }

    interface View{
        fun sendId(id: String)

    }


}