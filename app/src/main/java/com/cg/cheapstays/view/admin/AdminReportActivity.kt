package com.cg.cheapstays.view.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.MakeSnackBar
import com.cg.cheapstays.model.Rooms
import com.cg.cheapstays.presenter.admin.AdminReportPresenter
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_admin_report.*
import java.text.SimpleDateFormat
import java.util.*

class AdminReportActivity : AppCompatActivity(),AdminReportPresenter.View {

    lateinit var presenter: AdminReportPresenter
    lateinit var hotelId:String
    lateinit var rooms: Rooms
    lateinit var userIDList : MutableSet<String>
    lateinit var userNameList : MutableList<String>
    lateinit var adapter :ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report)

        adminReportCV.visibility =View.INVISIBLE
        presenter = AdminReportPresenter(this)
        presenter.initialize()
        hotelId = intent.getStringExtra("hotelid")!!
        userIDList = mutableSetOf()
        userNameList = mutableListOf()
        adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userNameList)

        presenter.getHotelRoomFireBase(hotelId)


        //BUTTON
        adminReportDateB.setOnClickListener {
            userNameList.clear()
            adminReportList.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userNameList)

            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.addOnPositiveButtonClickListener {
                val bookingDateInMillis = it.toString()
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                adminReportDateT.text = " $date"
                showBookings(bookingDateInMillis,date)
            }
            picker.show(supportFragmentManager,"null")

        }

        adminReportGuestB.setOnClickListener{
            userNameList.clear()
            if(userIDList.isEmpty())
                MakeSnackBar(findViewById(android.R.id.content)).make("No Bookings/Guests").show()
            presenter.getGuestList(userIDList)
        }


    }

    private fun showBookings(bookingDateInMillis: String, date: String) {
        presenter.getBookings(hotelId,bookingDateInMillis,date)
    }

    override fun getHotelRoomStatus(msg: String, name: String, single: String, double: String) {
        if(msg == "Success"){
            adminReportHotelNameT.text =name
            adminSingleRoomNoT.text = single
            adminDoubleRoomNoT.text = double
        }
        else{
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        }
    }

    override fun makeChanges(numS: String, numD: String, ids: MutableSet<String>) {
        adminReportCV.visibility = View.VISIBLE
        adminSingleRoomBookedT.text= numS
        adminDoubleRoomBookedT.text= numD
        userIDList =ids
        if(userIDList.isEmpty())
            MakeSnackBar(findViewById(android.R.id.content)).make("No Bookings/Guests").show()
    }

    override fun guestListStatus(userNames: MutableList<String>) {
        userNameList = userNames
        Log.d("AdminReport User",userNameList.toString())
        adminReportList.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userNameList)
    }

}