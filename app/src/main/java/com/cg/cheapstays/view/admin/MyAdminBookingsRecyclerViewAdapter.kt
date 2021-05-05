package com.cg.cheapstays.view.admin
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.presenter.admin.AdminBookingsPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.isOnline
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyAdminBookingsRecyclerViewAdapter(
    private val values: MutableList<Bookings>
) : RecyclerView.Adapter<MyAdminBookingsRecyclerViewAdapter.ViewHolder>() {
    lateinit var fDatabase: FirebaseDatabase
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_booking_list_item, parent, false)
        fDatabase = FirebaseDatabase.getInstance()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.date.text = item.date
        holder.price.text = "₹${item.totalPrice}"
        val ref = AdminBookingsPresenter.fDatabase.reference.child("hotels").child(item.hotelId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("name").exists()) holder.hotel.text = snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        val ref1 = AdminBookingsPresenter.fDatabase.reference.child("users").child(item.uid)
        ref1.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("name").exists()) holder.user.text = snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val user = view.findViewById<TextView>(R.id.show_booking_userT)
        val date = view.findViewById<TextView>(R.id.show_booking_dateT)
        val hotel = view.findViewById<TextView>(R.id.show_booking_hotelT)
        val price = view.findViewById<TextView>(R.id.show_booking_priceT)
    }
}