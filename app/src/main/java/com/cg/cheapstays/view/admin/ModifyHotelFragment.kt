package com.cg.cheapstays.view.admin

import android.app.AlertDialog
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
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_hotel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ModifyHotelFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
                        Log.d("HotelId","$hotelId")
                        spinnerAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_dropdown_item, hotelNames)
                        spinnerModidyHotel.adapter = spinnerAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity, "No Hotels Added", Toast.LENGTH_LONG).show()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ModifyHotelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ModifyHotelFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
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