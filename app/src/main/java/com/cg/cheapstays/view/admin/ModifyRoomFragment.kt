package com.cg.cheapstays.view.admin

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.math.max
import kotlin.math.min


class ModifyRoomFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var hotelid : String
    lateinit var fDatabase: FirebaseDatabase
    lateinit var dRef: DatabaseReference
    lateinit var roomId : MutableList<String>
    lateinit var oldPrice : String
    lateinit var listener: ValueEventListener
    var singlePrice = 0
    var doublePrice = 0
    var singleRoomNo = 0
    var singleBooked = 0
    lateinit var singleBookedDate : String
    var doubleRoomNo = 0
    var doubleBooked = 0
    lateinit var doubleBookedDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hotelid = it.getString("hotelid")!!
        }

    }

    override fun onResume() {
        super.onResume()
        deleteRoomsBtn.visibility = View.VISIBLE
        editRoomBtn.visibility = View.VISIBLE
        editRoomTariff.isFocusable = true
        editRoomsNo.isFocusable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fDatabase = FirebaseDatabase.getInstance()
        dRef = fDatabase.reference.child("hotels").child(hotelid)
        roomId = mutableListOf<String>()
        // Inflate the layout for this fragment

        dRef.child("rooms").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTime = Calendar.getInstance().timeInMillis
                if(snapshot.child("singleBooked").exists()){
                    for(childs in snapshot.child("singleBooked").children){
                        if(childs.key?.toLong()!! > currentTime){
                            val bookedRooms = childs.child("bookedRooms").value.toString().toInt()
                            if(bookedRooms>singleBooked){
                                singleBooked = bookedRooms
                                singleBookedDate = SimpleDateFormat("MMM dd, yyy", Locale.getDefault()).format(childs.key?.toLong())
                            }
                        }
                    }
                    for(childs in snapshot.child("doubleBooked").children){
                        if(childs.key?.toLong()!! > currentTime){
                            val bookedRooms = childs.child("bookedRooms").value.toString().toInt()
                            if(bookedRooms>doubleBooked){
                                doubleBooked = bookedRooms
                                doubleBookedDate = SimpleDateFormat("MMM dd, yyy", Locale.getDefault()).format(childs.key?.toLong())
                            }
                        }
                    }
                    Log.d("BookedRooms","$singleBookedDate,$doubleBookedDate")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })

        return inflater.inflate(R.layout.fragment_modify_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = dRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    oldPrice = snapshot.child("price").value.toString()
                    currentRoomHotel.setText(snapshot.child("name").value.toString())
                    if(snapshot.child("rooms").child("single").exists()){
                        singleRoomNo = snapshot.child("rooms").child("single").child("noOfRooms").value.toString().toInt()
                        singlePrice = snapshot.child("rooms").child("single").child("tariff").value.toString().toInt()
                    }
                    else{
                        onNullRooms()
                    }
                    if(snapshot.child("rooms").child("double").exists()){
                        doubleRoomNo = snapshot.child("rooms").child("double").child("noOfRooms").value.toString().toInt()
                        doublePrice = snapshot.child("rooms").child("double").child("tariff").value.toString().toInt()
                    }
                    else{
                        onNullRooms()
                    }
                    editRoomType.adapter = ArrayAdapter<String>(activity?.applicationContext!!,android.R.layout.simple_spinner_dropdown_item, arrayOf<String>("Single","Double"))
                    Log.d("Rooms","$singlePrice,$singleRoomNo,$doublePrice,$doubleRoomNo")
                    dRef.removeEventListener(this)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        editRoomType.onItemSelectedListener = this
        editRoomBtn.setOnClickListener {
            val roomType = editRoomType.selectedItem.toString().toLowerCase()
            val price = editRoomTariff.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                dRef.child("rooms").child(roomType).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            MaterialAlertDialogBuilder(activity as Context)
                                .setTitle("Confirmation")
                                .setMessage("Do you want to change the tariff of ${roomType.capitalize()} type from $singlePrice to $price?")
                                .setPositiveButton("Confirm"){ dialogInterface: DialogInterface, i: Int ->
                                        dRef.child("rooms").child(roomType).child("tariff").setValue(price)
                                }
                                .setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->
                                    dialogInterface.dismiss()
                                }.show()
                        }else{
                            //Toast.makeText(activity,"$snapshot",Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        //
                    }
                })
                delay(500)
                dRef.child("price").setValue(min(singlePrice,doublePrice))
            }

        }
        deleteRoomsBtn.setOnClickListener {
            val newRoomNo = editRoomsNo.text.toString().toInt()
            val roomType = editRoomType.selectedItem.toString().toLowerCase()
            val bookedRooms = if(roomType=="single") singleBooked else doubleBooked
            val bookedRoomsDate = if(roomType=="single") singleBookedDate else doubleBookedDate

            if(newRoomNo<bookedRooms){
                val sb = Snackbar.make(view,"This type of room can't be lesser than $bookedRooms as it is booked on $bookedRoomsDate",Snackbar.LENGTH_SHORT)
                sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                sb.duration = 4000
                sb.show()
            }
            else{
                MaterialAlertDialogBuilder(activity as Context)
                    .setTitle("Confirmation")
                    .setMessage("Do you want to change the number of rooms to $newRoomNo?")
                    .setPositiveButton("Confirm"){ dialogInterface: DialogInterface, i: Int ->
                        dRef.child("rooms").child(roomType).child("noOfRooms").setValue(newRoomNo)                    }
                    .setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }.show()
            }
        }

        addRoomsBtn2.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("hotelid",hotelid)
            val frag = AddRoomFragment()
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this)
                ?.replace(R.id.parentAdmin,frag)
                ?.commit()
        }

    }

    private fun onNullRooms() {
        deleteRoomsBtn.visibility = View.GONE
        editRoomBtn.visibility = View.GONE
        addRoomsBtn2.visibility = View.VISIBLE
        editRoomTariff.isFocusable = false
        editRoomsNo.isFocusable = false
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position==0) {
            editRoomTariff.setText("")
            editRoomTariff.setText(singlePrice.toString())
            editRoomsNo.setText(singleRoomNo.toString())
        }
        else    {
            editRoomTariff.setText("")
            editRoomTariff.setText(doublePrice.toString())
            editRoomsNo.setText(doubleRoomNo.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

    override fun onDetach() {
        super.onDetach()
        dRef.removeEventListener(listener)
    }
}