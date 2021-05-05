package com.cg.cheapstays.view.admin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Doubles
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.model.Single
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.isOnline
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class AddRoomFragment : Fragment() {
    lateinit var hotelid : String
    lateinit var fDatabase: FirebaseDatabase
    lateinit var ref : DatabaseReference
    var sp = Int.MAX_VALUE
    var dp = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
        arguments?.let {
            hotelid = it.getString("hotelid")!!
        }
        fDatabase = FirebaseDatabase.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        ref = fDatabase.reference.child("hotels").child(hotelid).child("rooms")

        return inflater.inflate(R.layout.fragment_add_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomTypeAdapter = ArrayAdapter<String>(activity as Context,android.R.layout.simple_spinner_dropdown_item, arrayOf("Single","Double"))
        val roomNoAdapter = ArrayAdapter<Int>(activity as Context,android.R.layout.simple_spinner_dropdown_item, arrayOf(1,2,3,4,5))
        addRoomType.adapter = roomTypeAdapter
        addNoOfBeds.adapter = roomNoAdapter

        addRoomBtn.setOnClickListener {
            if(roomPrice.text.isNotEmpty()){
                var noOfRooms = addNoOfBeds.selectedItem.toString().toInt()
                var price = roomPrice.text.toString()
                val roomType = addRoomType.selectedItem.toString().toLowerCase(Locale.ROOT)
                CoroutineScope(Dispatchers.IO).launch {
                    ref.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.child(roomType).exists()){
                                val rooms = snapshot.child(roomType).child("noOfRooms").value.toString().toInt()
                                noOfRooms+=rooms
                            }

                            if(roomType=="single")  {
                                ref.child(roomType).setValue(Single(price,noOfRooms))
                            }
                            else    {
                                ref.child(roomType).setValue(Doubles(price,noOfRooms))
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            //
                        }

                    })

                    delay(500)

                    ref.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if(snapshot.child("single").exists())   {
                                sp = snapshot.child("single").child("tariff").value.toString().toInt()
                                ref.parent?.child("price")?.setValue(min(sp,dp))
                            }
                            if(snapshot.child("double").exists())   {
                                dp = snapshot.child("double").child("tariff").value.toString().toInt()
                                ref.parent?.child("price")?.setValue(min(sp,dp))
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            //
                        }

                    })
                }


                roomReturnBtn.visibility = View.VISIBLE
            }else{
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Please enter the tariff details").show()
            }
        }
        roomReturnBtn.setOnClickListener {

            MaterialAlertDialogBuilder(it.context).setTitle("Confirmation")
                .setMessage("Do you want to return to main screen?")
                .setPositiveButton("Yes"){ _: DialogInterface, _: Int ->
                    startActivity(Intent(activity,AdminActivity::class.java))
                    activity?.finish()
                }.setNegativeButton("No"){ dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }.show()

        }

    }
}