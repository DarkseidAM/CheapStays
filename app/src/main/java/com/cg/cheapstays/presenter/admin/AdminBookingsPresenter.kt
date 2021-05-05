package com.cg.cheapstays.presenter.admin
import com.cg.cheapstays.model.Bookings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class AdminBookingsPresenter (val view : View) {
    companion object{
        lateinit var fDatabase: FirebaseDatabase
    }
    // Initializing Firebase
    fun initialize(){
        fDatabase = FirebaseDatabase.getInstance()
    }
    // Getting all the bookings made through the app
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
                // Callback to activity to change adapter
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
