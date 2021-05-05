package com.cg.cheapstays.view.admin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.view.utils.isOnline
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class ModifyRoomFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var hotelid : String
    lateinit var fDatabase: FirebaseDatabase
    lateinit var dRef: DatabaseReference
    lateinit var roomId : MutableList<String>
    lateinit var oldPrice : String
    lateinit var listener1 : ValueEventListener
    lateinit var listener2 : ValueEventListener
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
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
        arguments?.let {
            hotelid = it.getString("hotelid")!!
        }
        singleBookedDate = ""
        doubleBookedDate = ""
    }

    override fun onResume() {
        super.onResume()
        deleteRoomsBtn.visibility = View.VISIBLE
        editRoomBtn.visibility = View.VISIBLE
        editRoomTariff.isEnabled = true
        editRoomsNo.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fDatabase = FirebaseDatabase.getInstance()
        dRef = fDatabase.reference.child("hotels").child(hotelid)
        roomId = mutableListOf<String>()
        // Inflate the layout for this fragment

        listener2 = dRef.child("rooms").addValueEventListener(object : ValueEventListener{
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

        listener1 = dRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    oldPrice = snapshot.child("price").value.toString()
                    currentRoomHotel.setText(snapshot.child("name").value.toString())
                    if(snapshot.child("rooms").child("single").exists()){
                        singleRoomNo = snapshot.child("rooms").child("single").child("noOfRooms").value.toString().toInt()
                        singlePrice = snapshot.child("rooms").child("single").child("tariff").value.toString().toInt()
                    }
                    if(snapshot.child("rooms").child("double").exists()){
                        doubleRoomNo = snapshot.child("rooms").child("double").child("noOfRooms").value.toString().toInt()
                        doublePrice = snapshot.child("rooms").child("double").child("tariff").value.toString().toInt()
                    }
                    if(singleRoomNo==0){
                        onNullRooms()
                    }
                    editRoomType.adapter = ArrayAdapter<String>(activity?.applicationContext!!,android.R.layout.simple_spinner_dropdown_item, arrayOf<String>("Single","Double"))

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

            CoroutineScope(Dispatchers.Default).launch {
                val j = CoroutineScope(Dispatchers.IO).launch {
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
                }

                j.join()
                delay(1500)
                dRef.child("price").setValue(min(singlePrice,doublePrice))
            }

        }
        deleteRoomsBtn.setOnClickListener {
            val newRoomNo = editRoomsNo.text.toString().toInt()
            val roomType = editRoomType.selectedItem.toString().toLowerCase()
            val bookedRooms = if(roomType=="single") singleBooked else doubleBooked
            val bookedRoomsDate = if(roomType=="single") singleBookedDate else doubleBookedDate

            if(newRoomNo<bookedRooms){
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("This type of room can't be lesser than $bookedRooms as it is booked on $bookedRoomsDate").show()
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
        editRoomTariff.isEnabled = false
        editRoomsNo.isEnabled = false
    }
    private fun onNonNullRooms() {
        deleteRoomsBtn.visibility = View.VISIBLE
        editRoomBtn.visibility = View.VISIBLE
        addRoomsBtn2.visibility = View.GONE
        editRoomTariff.isFocusable = true
        editRoomsNo.isFocusable = true
        editRoomTariff.isEnabled = true
        editRoomsNo.isEnabled = true
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position==0) {
            if(singleRoomNo==0){
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("No single room available, Please add them first").show()
                onNullRooms()
            }
            else    onNonNullRooms()
            editRoomTariff.setText("")
            editRoomTariff.setText(singlePrice.toString())
            editRoomsNo.setText(singleRoomNo.toString())
        }
        else{
            if(doubleRoomNo==0) {
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("No double room available, Please add them first").show()
                onNullRooms()
            }
            else onNonNullRooms()
            editRoomTariff.setText("")
            editRoomTariff.setText(doublePrice.toString())
            editRoomsNo.setText(doubleRoomNo.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dRef.removeEventListener(listener1)
        dRef.removeEventListener(listener2)
    }
}