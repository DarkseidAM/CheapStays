package com.cg.cheapstays.presenter.admin

import android.util.Log
import com.cg.cheapstays.model.Rooms
import com.cg.cheapstays.model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminReportPresenter (val view : View) {

    companion object{
        lateinit var fDatabase: FirebaseDatabase
    }

    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
    }


    fun getHotelRoomFireBase(hotelId :String){
        val ref = fDatabase.reference.child("hotels").child(hotelId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val name = snapshot.child("name").value.toString()
                    val rooms = snapshot.child("rooms").getValue(Rooms::class.java)?: Rooms()
                    view.getHotelRoomStatus("Success",name,"${rooms.single.noOfRooms} (₹${rooms.single.tariff})","${rooms.double.noOfRooms} (₹${rooms.double.tariff})")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                view.getHotelRoomStatus("${error.message}","","","")
            }
        })
    }


    fun getGuestList(userIDList: MutableSet<String>) {
        val userNameList = mutableListOf<String>()
        userNameList.clear()
        for(user in userIDList) {
            var ref = fDatabase.reference.child("users").child(user)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Users::class.java)!!
                        userNameList.add(user.name)
                        if(userNameList.size == userIDList.size)
                        {
                            Log.d("AdminReport User",userNameList.toString())
                            view.guestListStatus(userNameList)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    }

    fun getBookings(hotelId: String, bookingDateInMillis: String, date: String)
    {
        var numS ="0"
        var numD ="0"
        val userIDList = mutableSetOf<String>()

        var ref = fDatabase.reference.child("hotels").child(hotelId).child("rooms").child("doubleBooked").child(bookingDateInMillis)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    numD = snapshot.child("bookedRooms").value.toString()
                    Log.d("AdminReport 1","$numS $numD \n $userIDList")
                }
                else{
                    numD = "0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        var ref1 = fDatabase.reference.child("hotels").child(hotelId).child("rooms").child("singleBooked").child(bookingDateInMillis)
        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    numS = snapshot.child("bookedRooms").value.toString()
                    Log.d("AdminReport 2","$numS $numD \n $userIDList")
                }
                else{
                    numS = "0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val refU = fDatabase.reference.child("bookings").orderByChild("date").equalTo(date)
        refU.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(child in snapshot.children){
                        val userId = child.child("uid").value.toString()
                        userIDList.add(userId)
                    }
                    Log.d("AdminReport 3","$numS $numD \n $userIDList")
                    view.makeChanges(numS,numD,userIDList)
                }
                else{
                    Log.d("AdminReport 3","$numS $numD \n $userIDList")
                    view.makeChanges(numS,numD,userIDList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    interface View {
        fun getHotelRoomStatus(msg: String, name: String, single: String, double: String)
        fun makeChanges(numS: String, numD: String, userIDList: MutableSet<String>)
        fun guestListStatus(userNameList: MutableList<String>)

    }
}