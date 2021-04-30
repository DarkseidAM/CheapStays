package com.cg.cheapstays.view.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Doubles
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.model.Single
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRoomFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var hotelid : String
    lateinit var fDatabase: FirebaseDatabase
    lateinit var ref : DatabaseReference
    var sp = Int.MAX_VALUE
    var dp = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        val roomTypeAdapter = ArrayAdapter<String>(activity as Context,android.R.layout.simple_spinner_item, arrayOf("Single","Double"))
        val roomNoAdapter = ArrayAdapter<Int>(activity as Context,android.R.layout.simple_spinner_item, arrayOf(1,2,3,4,5))
        addRoomType.adapter = roomTypeAdapter
        addNoOfBeds.adapter = roomNoAdapter
        var price = Double.MAX_VALUE

        addRoomBtn.setOnClickListener {
            if(roomPrice.text.isNotEmpty()){
                var noOfRooms = addNoOfBeds.selectedItem.toString().toInt()
                var price = roomPrice.text.toString()
                val roomType = addRoomType.selectedItem.toString().toLowerCase(Locale.ROOT)
                var oldPrice = Int.MAX_VALUE
                CoroutineScope(Dispatchers.IO).launch {
                    ref.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.child(roomType).exists()){
                                val rooms = snapshot.child(roomType).child("noOfRooms").value.toString().toInt()
                                Log.d("OldPrice","$rooms")
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

                    delay(1000)

                    ref.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if(snapshot.child("single").exists())   {
                                sp = snapshot.child("single").child("tariff").value.toString().toInt()
                                Log.d("Pricess","$sp $dp")
                                ref.parent?.child("price")?.setValue(min(sp,dp))
                            }
                            if(snapshot.child("double").exists())   {
                                dp = snapshot.child("double").child("tariff").value.toString().toInt()
                                Log.d("Pricess","$sp $dp")
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
                Toast.makeText(activity,"Please enter the tariff details",Toast.LENGTH_LONG).show()
            }
        }
        roomReturnBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to return to main screen?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->

                startActivity(Intent(activity,AdminActivity::class.java))
                activity?.finish()

            })
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}