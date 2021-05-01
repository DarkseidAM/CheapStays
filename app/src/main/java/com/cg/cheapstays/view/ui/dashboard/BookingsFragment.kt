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
/**
 * A fragment representing a list of Items.
 */
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
            columnCount = it.getInt(ARG_COLUMN_COUNT)
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
                        //Sets adapter
                        if(view is RecyclerView){
                            view.adapter = MyBookingsRecyclerViewAdapter(bookingList)
                        }
                    }
                }
                else{
                    Toast.makeText(activity,"No Bookings",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            BookingsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}