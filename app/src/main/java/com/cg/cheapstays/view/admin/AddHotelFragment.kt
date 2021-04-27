package com.cg.cheapstays.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_hotel.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddHotelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddHotelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var fDatabase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fDatabase = FirebaseDatabase.getInstance()
        return inflater.inflate(R.layout.fragment_add_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addHotelBtn.setOnClickListener {
            if(addHotelName.text.isNotEmpty() && addHotelAddress.text.isNotEmpty() && addHotelRooms.text.isNotEmpty() && addHotelDesc.text.isNotEmpty()){
                val db = fDatabase.reference.child("hotels")
                val hotelid = db.push().key!!
                val hotel = Hotels(addHotelName.text.toString(),addHotelAddress.text.toString(),addHotelDesc.text.toString(),addHotelRooms.text.toString().toInt(),
                    listOf<Hotels.Rooms>())
                db.child(hotelid).setValue(hotel).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(activity,"Added hotel successfully",Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(activity,"${it.exception?.message}",Toast.LENGTH_LONG).show()
                    }
                }

            }else{
                Toast.makeText(activity,"Please enter all the details",Toast.LENGTH_LONG).show()
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
         * @return A new instance of fragment AddHotelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddHotelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}