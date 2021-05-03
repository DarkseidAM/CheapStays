package com.cg.cheapstays.view.ui.home.presenter
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelsPresenter(val view : View) {
    companion object{
        lateinit var fDatabase: FirebaseDatabase
    }
    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
    }
    fun getHotelList(){
        val ref = fDatabase.reference.child("hotels")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            val hotelList = mutableListOf<Hotels>()
            val hotelMap = mutableMapOf<Hotels,String>()
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val hotel = child.getValue(Hotels::class.java)
                        hotelList.add(hotel!!)
                        hotelMap[hotel!!] = child.key.toString()
                    }
                }
                view.changeHotelAdapter(hotelList,hotelMap)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    interface View {
        abstract fun changeHotelAdapter(hotelList: MutableList<Hotels>, hotelId: MutableMap<Hotels, String>)
    }
}