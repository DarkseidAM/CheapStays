package com.cg.cheapstays.view.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.model.MakeSnackBar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_hotel.*



class ModifyHotelFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var fDatabase: FirebaseDatabase
    lateinit var dRef: DatabaseReference
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var hotelList: MutableList<Hotels>
    lateinit var hotelNames : MutableList<String>
    lateinit var hotelId : MutableList<String>
    var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        hotelId = mutableListOf()
        hotelList = mutableListOf()
        hotelNames = mutableListOf()
        fDatabase = FirebaseDatabase.getInstance()
        dRef = fDatabase.reference.child("hotels")

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        spinnerAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        return inflater.inflate(R.layout.fragment_modify_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

                dRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            hotelList.clear()
                            hotelNames.clear()
                            hotelId.clear()
                            for (child in snapshot.children) {
                                val hotel = child.getValue(Hotels::class.java)
                                hotelId.add(child.key.toString())
                                hotelList.add(hotel!!)
                                hotelNames.add(hotel.name)
                            }
                        }
                        spinnerAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_dropdown_item, hotelNames)
                        spinnerModidyHotel.adapter = spinnerAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("No Hotels Added").show()
                    }

                })



        spinnerModidyHotel.onItemSelectedListener = this

        editHotelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you confirm the changes")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                modifyHotel(hotelId[currentPosition])
            })
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()
        }
        deleteHotel.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Are you sure?")
            builder.setMessage("You won't be able to revert the changes")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                dRef.child(hotelId[currentPosition]).removeValue()
            })
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()
        }
        modifyHotelRoomBtn.setOnClickListener {
            val frag = ModifyRoomFragment()
            val bundle = Bundle()
            frag.arguments = bundle
            bundle.putString("hotelid",hotelId[currentPosition])

            activity?.supportFragmentManager?.beginTransaction()
                    ?.remove(this)
                    ?.replace(R.id.parentAdmin,frag)
                    ?.commit()
        }

    }


    private fun modifyHotel(id: String) {
        val hotel = dRef.child(id)
        hotel.child("name").setValue(editHotelName.text.toString())
        hotel.child("address").setValue(editHotelAddress.text.toString())
        hotel.child("description").setValue(editHotelDesc.text.toString())
        hotel.child("specialOffer").setValue(modifyHotelOffer.text.toString())
        spinnerAdapter.notifyDataSetChanged()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        editHotelName.setText(hotelList[position].name)
        editHotelAddress.setText(hotelList[position].address)
        editHotelDesc.setText(hotelList[position].description)
        modifyHotelOffer.setText(hotelList[position].specialOffer)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }


}