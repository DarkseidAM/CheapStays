package com.cg.cheapstays.view.admin.presenter

import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_modify_hotel.*

class ModifyHotelPresenter(val view: View) {

    companion object{
        lateinit var fDatabase : FirebaseDatabase
    }

    fun initialize() {
        fDatabase = FirebaseDatabase.getInstance()
    }

    fun getHotelsFireBase(){
        val dRef = fDatabase.reference.child("hotels")
        val hotelList = mutableListOf<Hotels>()
        val hotelNames = mutableListOf<String>()
        val hotelId = mutableListOf<String>()

        dRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

    interface View {
        fun getHotelsStatus(msg: String, hotelNames: MutableList<String>, hotelId: MutableList<String>, hotelList: MutableList<Hotels>)
        fun modifyHotelStatus(msg: String)
        fun removeHotelStatus(msg: String)
    }
}