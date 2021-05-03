package com.cg.cheapstays.view.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.cg.cheapstays.model.MakeSnackBar
import com.cg.cheapstays.model.Rooms
import com.cg.cheapstays.model.Users
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin_report.*
import java.text.SimpleDateFormat
import java.util.*

class AdminReportActivity : AppCompatActivity() {

    lateinit var fDatabase: FirebaseDatabase
    lateinit var hotelId:String
    lateinit var rooms: Rooms
    lateinit var userIDList : MutableSet<String>
    lateinit var userNameList : MutableList<String>
    lateinit var adapter :ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report)

        adminReportCV.visibility =View.INVISIBLE
        fDatabase = FirebaseDatabase.getInstance()
        hotelId = intent.getStringExtra("hotelid")!!
        userIDList = mutableSetOf()
        userNameList = mutableListOf()
        adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userNameList)

        val ref = fDatabase.reference.child("hotels").child(hotelId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val name = snapshot.child("name").value.toString()
                    adminReportHotelNameT.text = name
                    rooms = snapshot.child("rooms").getValue(Rooms::class.java)?: Rooms()
                    adminSingleRoomNoT.text = "${rooms.single.noOfRooms} (₹${rooms.single.tariff})"
                    adminDoubleRoomNoT.text = "${rooms.double.noOfRooms} (₹${rooms.double.tariff})"
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


        //BUTTON
        adminReportDateB.setOnClickListener {
            userNameList.clear()
            userIDList.clear()
            adapter.notifyDataSetChanged()
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.addOnPositiveButtonClickListener {
                val bookingDateInMillis = it.toString()
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                adminReportDateT.text = " $date"
                showBookings(bookingDateInMillis,date)
            }
            picker.show(supportFragmentManager,"null")

        }

        adminReportGuestB.setOnClickListener{
            userNameList.clear()
            adminReportList.adapter =adapter
            if(userIDList.isEmpty())
                MakeSnackBar(findViewById(android.R.id.content)).make("No Bookings/Guests").show()
            for(user in userIDList){
                var ref = fDatabase.reference.child("users").child(user)
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            val user = snapshot.getValue(Users::class.java)!!
                            userNameList.add(user.name)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }

        }


    }

    private fun showBookings(bookingDateInMillis: String, date: String) {
        var ref = fDatabase.reference.child("hotels").child(hotelId).child("rooms").child("doubleBooked").child(bookingDateInMillis)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) adminDoubleRoomBookedT.text= snapshot.child("bookedRooms").value.toString()
                else adminDoubleRoomBookedT.text= "0"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        ref = fDatabase.reference.child("hotels").child(hotelId).child("rooms").child("singleBooked").child(bookingDateInMillis)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) adminSingleRoomBookedT.text= snapshot.child("bookedRooms").value.toString()
                else adminSingleRoomBookedT.text= "0"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        val refU = fDatabase.reference.child("bookings").orderByChild("date").equalTo(date)
        refU.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminReportCV.visibility =View.VISIBLE
                if(snapshot.exists()){
                    userIDList.clear()
                    for(child in snapshot.children){
                        val userId = child.child("uid").value.toString()
                        userIDList.add(userId)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}