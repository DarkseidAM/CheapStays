package com.cg.cheapstays.view.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_bookings_list.*
import java.text.SimpleDateFormat
import java.util.*


class BookingsFragment : Fragment() {
    private var columnCount = 1
    lateinit var fDatabase: FirebaseDatabase
    lateinit var bookingList : MutableList<Bookings>
    lateinit var bookingId : MutableList<String>
    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fDatabase = FirebaseDatabase.getInstance()
        bookingList = mutableListOf()
        bookingId = mutableListOf()
        fAuth = FirebaseAuth.getInstance()

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookings_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ref = fDatabase.reference.child("bookings").orderByChild("uid").equalTo(fAuth.currentUser?.uid.toString())
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    bookingList.clear()
                    bookingId.clear()
                    for(child in snapshot.children){
                        val bookings = child.getValue(Bookings::class.java)!!
                        bookingList.add(bookings)
                        bookingId.add(child.key.toString())
                        Log.d("Bookings","$bookingList")

                    }
                    if(view is RecyclerView){
                        view.adapter = MyBookingsRecyclerViewAdapter(bookingList){
                            val bookings = bookingList[it]
                            fDatabase.reference.child("bookings").child(bookingId[it]).removeValue()
                            view.adapter?.notifyDataSetChanged()
                            var time = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(bookings.date)?.time
                            time = time!! + (5.5*60*60*1000).toInt()
                            val bookedRoomType = if(bookings.roomType == "single")  "singleBooked"  else    "doubleBooked"
                            val dRef = fDatabase.reference.child("hotels").child(bookings.hotelId).child("rooms")
                                    .child(bookedRoomType).child((time).toString())
                            dRef.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    Log.d("NewRooms","$dRef")
                                    if(snapshot.exists()){
                                        val current = snapshot.child("bookedRooms").value.toString().toInt()
                                        val new = current - bookings.noOfRooms
                                        Log.d("NewRooms","$new")
                                        fDatabase.reference.child("hotels").child(bookings.hotelId).child("rooms")
                                                .child(bookedRoomType).child(time.toString())
                                                .updateChildren(mutableMapOf("bookedRooms" to new as Any))
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    //
                                }
                            })

                        }
                    }
                }
                else{
                    Toast.makeText(view.context,"No Bookings",Toast.LENGTH_SHORT).show()
                    view.findViewById<RecyclerView>(R.id.bookings_list2).adapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }


}