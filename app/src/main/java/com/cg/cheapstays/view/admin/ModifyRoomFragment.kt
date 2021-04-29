package com.cg.cheapstays.view.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_room.*

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

        val listener = dRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    oldPrice = snapshot.child("price").value.toString()
                    currentRoomHotel.setText(snapshot.child("name").value.toString())
                    for(childs in snapshot.child("rooms").children){
                        roomId.add(childs.key.toString())
                        if(childs.child("type").value.toString() == "Single"){
                            ++singleRoomNo
                            singlePrice = childs.child("tariff").value.toString().toInt()
                        }else{
                            ++doubleRoomNo
                            doublePrice = childs.child("tariff").value.toString().toInt()
                        }
                    }
                    editRoomType.adapter = ArrayAdapter<String>(activity?.applicationContext!!,android.R.layout.simple_spinner_dropdown_item, arrayOf<String>("Single","Double"))
                    Log.d("Rooms","$roomId")
                    dRef.removeEventListener(this)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        editRoomType.onItemSelectedListener = this
        editRoomBtn.setOnClickListener {
            val roomType = editRoomType.selectedItem.toString()
            val price = editRoomTariff.text.toString()
            for(id in roomId){
                dRef.child("rooms").child(id).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("type").value.toString() == roomType) dRef.child("rooms").child(id).child("tariff").setValue(price.toString())
                            if(price.toInt() < oldPrice.toInt())    dRef.child("price").setValue(price.toInt())
                        }
                        dRef.removeEventListener(this)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //
                    }

                })
            }

        }

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
            editRoomTariff.setText(singlePrice.toString())
            editRoomsNo.setText(singleRoomNo.toString())
        }
        else    {
            editRoomTariff.setText(doublePrice.toString())
            editRoomsNo.setText(doubleRoomNo.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }
}