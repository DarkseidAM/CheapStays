package com.cg.cheapstays.presenter.admin

import android.util.Log
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.model.Users
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ModifyHotelPresenter(val view: View) {

    companion object{
        lateinit var fDatabase : FirebaseDatabase
        lateinit var listener: ValueEventListener
        lateinit var dRef : DatabaseReference
        var flag = 0
    }

    fun initialize() {
        fDatabase = FirebaseDatabase.getInstance()
    }

    fun getHotelsFireBase(){
        dRef = fDatabase.reference.child("hotels")
        val hotelList = mutableListOf<Hotels>()
        val hotelNames = mutableListOf<String>()
        val hotelId = mutableListOf<String>()

        listener = dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hotelList.clear()
                    hotelNames.clear()
                    hotelId.clear()
                    for (child in snapshot.children) {
                        val hotel = child.getValue(Hotels::class.java)
                        hotelId.add(child.key.toString())
                        hotelList.add(hotel!!)
                        hotelNames.add(hotel.name)
                    }
                }
                view.getHotelsStatus("Success",hotelNames,hotelId,hotelList)
            }

            override fun onCancelled(error: DatabaseError) {
                view.getHotelsStatus("Failure",hotelNames,hotelId,hotelList)
            }

        })
    }


    fun removeListener(){
        dRef.removeEventListener(listener)
    }


    fun modifyHotelFireBase(hotelId: String, name: String, addr: String, desc: String, offer: String) {

        val dRef = fDatabase.reference.child("hotels")
        val hotel = dRef.child(hotelId)

        hotel.child("name").setValue(name)
        hotel.child("address").setValue(addr)
        hotel.child("description").setValue(desc)
        hotel.child("specialOffer").setValue(offer)

        view.modifyHotelStatus("Success")
    }


    fun removeHotelFireBase(hotelId: String) {

        val dRef = fDatabase.reference.child("hotels")
        dRef.child(hotelId).removeValue().addOnSuccessListener {
            view.removeHotelStatus("Success")
        }
    }

    fun checkRemoving(hotelId : String){
        var currentTime = Calendar.getInstance().timeInMillis
        val ref1 = fDatabase.reference.child("bookings")
            .orderByChild("hotelId").equalTo(hotelId)
        ref1.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(childs in snapshot.children){
                        val booking = childs.getValue(Bookings::class.java)!!
                        val btime = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(booking.date)?.time!!
                        if(hotelId == booking.hotelId && btime>currentTime){
                            flag = 1
                            ref1.removeEventListener(this)
                        }
                        Log.d("HotelBooking","$flag")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }

        })
        val ref2 = fDatabase.reference.child("users").orderByChild("userType").equalTo("employee")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(childs in snapshot.children){
                        val users = childs.getValue(Users::class.java)!!
                        if(childs.child("hotelId").value.toString()==hotelId){
                            flag = 2
                            ref2.removeEventListener(this)
                            Log.d("EmployeePresent","$flag")
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }

        })
    }

    interface View {
        fun getHotelsStatus(msg: String, hotelNames: MutableList<String>, hotelId: MutableList<String>, hotelList: MutableList<Hotels>)
        fun modifyHotelStatus(msg: String)
        fun removeHotelStatus(msg: String)
        fun hotelRemoveConditions(flag : Int)
    }
}