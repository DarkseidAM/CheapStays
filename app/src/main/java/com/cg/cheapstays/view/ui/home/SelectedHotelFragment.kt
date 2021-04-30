package com.cg.cheapstays.view.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.model.Rooms
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_selected_hotel.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectedHotelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectedHotelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var hotelId : String
    lateinit var bookingDate : String
    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var dRef : DatabaseReference
    lateinit var hotel : Hotels
    lateinit var room : Rooms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            hotelId = it.getString("hotelid")!!
        }
        fDatabase = FirebaseDatabase.getInstance()
        fAuth= FirebaseAuth.getInstance()
        dRef = fDatabase.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        Toast.makeText(activity,"$hotelId",Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_selected_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dRef.child("hotels").child(hotelId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hotel = snapshot.getValue(Hotels::class.java)!!
                    if(snapshot.child("rooms").exists()){
                        room = snapshot.child("rooms").getValue(Rooms::class.java)!!
                    }
                    hotelNameT.setText(hotel?.name)
                    Glide.with(activity!!).load(Uri.parse(hotel?.imgPath)).placeholder(R.drawable.default_hotel).into(imageView6)
                    hotelRatingT.setText(hotel?.rating.toString())
                    hotelPriceT.setText(hotel?.price.toString())
                    hotelAddressT.setText(hotel?.address)
                    hotelDescT.setText(hotel?.description)
                    hotelOfferT.setText(hotel?.specialOffer)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })
        val roomTypeAdapter = ArrayAdapter<String>(activity as Context,android.R.layout.simple_spinner_item, arrayOf("Single","Double"))
        val roomNoAdapter = ArrayAdapter<Int>(activity as Context,android.R.layout.simple_spinner_item, arrayOf(1,2,3,4,5))
        book_room_type.adapter = roomTypeAdapter
        book_noOfRooms.adapter = roomNoAdapter
        book_room_btn.setOnClickListener {

            val selectedNoOfRooms = book_noOfRooms.selectedItem.toString().toInt()
            val selectedTypeRooms = book_room_type.selectedItem.toString().toLowerCase()
            val totalPrice  = if(selectedTypeRooms == "single") (selectedNoOfRooms*room.single.tariff.toInt()) else (selectedNoOfRooms.toInt()*room.double.tariff.toInt())

            val booking = Bookings(fAuth.currentUser?.uid.toString(),hotelId,bookingDate,selectedNoOfRooms,selectedTypeRooms,totalPrice.toDouble())
            dRef.child("bookings").child(UUID.randomUUID().toString()).setValue(booking)
        }

        book_from_date_btn.setOnClickListener {

            activity?.supportFragmentManager?.let {
                val picker = MaterialDatePicker.Builder.datePicker().build()
                picker.addOnPositiveButtonClickListener {
                val date = SimpleDateFormat("MMM dd, yyyy",Locale.getDefault()).format(Date(it))
                    textView12.text = " $date"
                bookingDate = date.toString()
                }
                picker.show(it,"")
            }

            }

        }


    }
