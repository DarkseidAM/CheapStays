package com.cg.cheapstays.view.admin

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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ModifyRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
    var doubleRoomNo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
//                    dRef.removeEventListener(this)

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
                            dRef.child("rooms").child(roomType).child("tariff").setValue(price)
                        }else{
                            Toast.makeText(activity,"$snapshot",Toast.LENGTH_LONG).show()
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
            dRef.child("rooms").child(roomType).child("noOfRooms").setValue(newRoomNo)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ModifyRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ModifyRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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