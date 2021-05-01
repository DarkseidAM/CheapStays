package com.cg.cheapstays.view.ui.dashboard

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings

class MyBookingsRecyclerViewAdapter(
        private val values: List<Bookings>)
    : RecyclerView.Adapter<MyBookingsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_bookings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        /* val ref = fDatabase.reference.child("hotels").child("${item.hotelId}")
         ref.addListenerForSingleValueEvent(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists())
                 {
                     val hotel = snapshot.getValue(Hotels::class.java)
                 }
             }
             override fun onCancelled(error: DatabaseError) {
                 //
             }
         })*/
        holder.idView.text = item.date
        holder.contentView.text = item.totalPrice.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)


    }
}