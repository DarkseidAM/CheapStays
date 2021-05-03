package com.cg.cheapstays.view.ui.home


import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cg.cheapstays.R
import com.cg.cheapstays.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_selected_hotel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SelectedHotelFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var hotelId : String
    lateinit var bookingDate : String
    lateinit var bookingDateInMillis : String
    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var dRef : DatabaseReference
    lateinit var hotel : Hotels
    lateinit var room : Rooms
    var currentBookedRooms = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hotelId = it.getString("hotelid")!!
        }
        fDatabase = FirebaseDatabase.getInstance()
        fAuth= FirebaseAuth.getInstance()
        dRef = fDatabase.reference
        room = Rooms(Single("",0), Doubles("",0))
        bookingDateInMillis = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_selected_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<Spinner>(R.id.book_room_type)?.isEnabled = false
        activity?.findViewById<Spinner>(R.id.book_noOfRooms)?.isEnabled = false

        dRef.child("hotels").child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hotel = snapshot.getValue(Hotels::class.java)!!
                    if(snapshot.child("rooms").exists()){
                        room = snapshot.child("rooms").getValue(Rooms::class.java)!!
                    }
                    hotelNameT.setText(hotel.name)
                    Glide.with(activity!!).load(Uri.parse(hotel.imgPath)).placeholder(R.drawable.default_hotel).into(imageView6)
                    hotelRatingT.setText(hotel.rating.toString())
                    hotelPriceT.setText(hotel.price.toString())
                    hotelAddressT.setText(hotel.address)
                    hotelDescT.setText(hotel.description)
                    hotelOfferT.setText(hotel.specialOffer)
                }
                dRef.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })
        val roomTypeAdapter = ArrayAdapter<String>(activity as Context,android.R.layout.simple_spinner_item, arrayOf("--SELECT--","Single","Double"))
        book_room_type.adapter = roomTypeAdapter
        book_room_btn.setOnClickListener {

            val selectedNoOfRooms = book_noOfRooms.selectedItem.toString().toInt()
            val selectedTypeRooms = book_room_type.selectedItem.toString().toLowerCase()
            val bookingRoomType = if(selectedTypeRooms=="single") "singleBooked" else "doubleBooked"
            val totalPrice  = if(selectedTypeRooms == "single") (selectedNoOfRooms*room.single.tariff.toInt()) else (selectedNoOfRooms.toInt()*room.double.tariff.toInt())

            MaterialAlertDialogBuilder(activity as Context)
                .setTitle("Confirmation")
                .setMessage("Do you want to confirm this booking \n₹$totalPrice on $bookingDate \nAt ${hotel.name}")
                .setPositiveButton("Book"){ dialogInterface: DialogInterface, i: Int ->
                    Toast.makeText(activity,"Booked",Toast.LENGTH_LONG).show()
                    val booking = Bookings(fAuth.currentUser?.uid.toString(),hotelId,bookingDate,selectedNoOfRooms,selectedTypeRooms,totalPrice.toDouble())
                    dRef.child("bookings").child(UUID.randomUUID().toString()).setValue(booking)
                    dRef.child("hotels").child(hotelId).child("rooms").child(bookingRoomType).child(bookingDateInMillis).child("bookedRooms").setValue(currentBookedRooms+selectedNoOfRooms)
//                    activity?.supportFragmentManager?.popBackStack()
                    activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.selectedItemId = R.id.navigation_dashboard
                }
                .setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }.show()



        }
        book_room_type.onItemSelectedListener = this

        book_date_btn.setOnClickListener {

            activity?.supportFragmentManager?.let {frag ->
                val picker = MaterialDatePicker.Builder.datePicker().build()
                picker.addOnPositiveButtonClickListener {
                    bookingDateInMillis = it.toString()
                    val date = SimpleDateFormat("MMM dd, yyyy",Locale.getDefault()).format(Date(it))
                    textView12.text = " $date"
                    bookingDate = date.toString()
                    if(it>0){
                        book_room_type.isEnabled = true
                        book_room_type.setSelection(0)
                    }
                }
                picker.show(frag,"")
            }

            }

        }

    override fun onResume() {
        super.onResume()
        view?.isFocusableInTouchMode =true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                val frag = HotelsFragment()
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.parent_home_linear,frag)
                        ?.commit()
                return@setOnKeyListener true
            }
            else{
                return@setOnKeyListener false
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val bookingRoomType = if(book_room_type.selectedItem.toString().toLowerCase() == "single") "singleBooked" else "doubleBooked"
        CoroutineScope(Dispatchers.Main).launch {
            if(bookingDateInMillis.isNotEmpty()) {
                dRef.child("hotels").child(hotelId).child("rooms").child(bookingRoomType)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child(bookingDateInMillis).exists()) {
                                    currentBookedRooms = snapshot.child(bookingDateInMillis)
                                        .child("bookedRooms").value.toString().toInt()
                                } else {
                                    currentBookedRooms = 0
                                    dRef.child("hotels").child(hotelId).child("rooms")
                                        .child(bookingRoomType).child(bookingDateInMillis)
                                        .child("bookedRooms").setValue(0)
                                }
                            } else {
                                currentBookedRooms = 0
                                dRef.child("hotels").child(hotelId).child("rooms")
                                    .child(bookingRoomType).child(bookingDateInMillis)
                                    .child("bookedRooms").setValue(0)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //
                        }
                    })
                delay(500)
                val allRooms = if (book_room_type.selectedItem.toString().toLowerCase() == "single") room.single.noOfRooms else room.double.noOfRooms
                val availableRooms = allRooms - currentBookedRooms
                Log.d("RoomsAv","$allRooms,$currentBookedRooms")
                if (availableRooms == 0) {
                    MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("No rooms available for this type on selected date").show()
                } else {
                    val intArray: List<Int> = IntRange(1, availableRooms).step(1).toList()
                    val roomNoAdapter = ArrayAdapter<Int>(
                        activity as Context,
                        android.R.layout.simple_spinner_item,
                        intArray
                    )
                    book_noOfRooms.adapter = roomNoAdapter
                    book_noOfRooms.isEnabled = true
                }
            }
        }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

}
