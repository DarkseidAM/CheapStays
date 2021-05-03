package com.cg.cheapstays.view.admin.presenter
import com.cg.cheapstays.model.Bookings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class AdminBookingsPresenter (val view : View) {
    companion object{
        lateinit var fDatabase: FirebaseDatabase
    }
    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
    }
    fun getBookingList(){
        val ref = fDatabase.reference.child("bookings").orderByChild("date")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            val bookingList = mutableListOf<Bookings>()
            val bookingMap = mutableMapOf<Bookings,String>()
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val booking = child.getValue(Bookings::class.java)!!
                        bookingList.add(booking)
                        bookingMap[booking] = child.key.toString()
                    }
                }
                view.changeBookingAdapter(bookingMap,bookingList)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    interface View {
        fun changeBookingAdapter(bookingMap: MutableMap<Bookings, String>, bookingList: MutableList<Bookings>)
    }
}
