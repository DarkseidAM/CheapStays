package com.cg.cheapstays.view.ui.dashboard

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBookingsRecyclerViewAdapter(
    private val values: List<Bookings>)
    : RecyclerView.Adapter<MyBookingsRecyclerViewAdapter.ViewHolder>() {
    lateinit var fDatabase : FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        fDatabase = FirebaseDatabase.getInstance()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_bookings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val ref = fDatabase.reference.child("hotels").child("${item.hotelId}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val hotel = snapshot.getValue(Hotels::class.java)
                    holder.hotelName.text = hotel?.name
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
        holder.date.text = item.date
        holder.price.text = "${item.totalPrice.toString()}₹"
        holder.roomDetails.text ="${item.noOfRooms} ${item.roomType} Rooms"
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hotelName = view.findViewById<TextView>(R.id.bookingListHotelNameT)
        val date = view.findViewById<TextView>(R.id.bookingListDateT)
        val roomDetails = view.findViewById<TextView>(R.id.bookingListRoomDetailsT)
        val price = view.findViewById<TextView>(R.id.bookingListPriceT)
    }
}