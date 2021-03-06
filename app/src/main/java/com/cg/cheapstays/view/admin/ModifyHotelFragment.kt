package com.cg.cheapstays.view.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.admin.ModifyHotelPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.MakeProgressBar
import com.cg.cheapstays.view.utils.isOnline
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_hotel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ModifyHotelFragment : Fragment(),
        ModifyHotelPresenter.View,
        AdapterView.OnItemSelectedListener {

    lateinit var presenter: ModifyHotelPresenter
    lateinit var fDatabase: FirebaseDatabase
    lateinit var dRef: DatabaseReference

    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var hotelList: MutableList<Hotels>
    lateinit var hotelNames : MutableList<String>
    lateinit var hotelId : MutableList<String>
    lateinit var pBar : ProgressBar
    var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
        arguments?.let {
        }
        hotelId = mutableListOf()
        hotelList = mutableListOf()
        hotelNames = mutableListOf()

        presenter = ModifyHotelPresenter(this)
        presenter.initialize()

        fDatabase = FirebaseDatabase.getInstance()
        dRef = fDatabase.reference.child("hotels")

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        pBar.visibility = View.VISIBLE
        presenter.getHotelsFireBase()
        spinnerModidyHotel.onItemSelectedListener = this


        editHotelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you confirm the changes")
            builder.setPositiveButton("Yes") { _, _ ->

                pBar.visibility = View.VISIBLE
                presenter.modifyHotelFireBase(hotelId[currentPosition],editHotelName.text.toString(),
                        editHotelAddress.text.toString(),
                        editHotelDesc.text.toString(),
                        modifyHotelOffer.text.toString())

            }
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()
        }

        deleteHotel.setOnClickListener {
            Toast.makeText(it.context,"Checking for any problems...",Toast.LENGTH_LONG).show()
            var flag = 0
            CoroutineScope(Dispatchers.Main).launch {
                pBar.visibility = View.VISIBLE
                val j = CoroutineScope(Dispatchers.IO).launch {
                    presenter.checkRemoving(hotelId[currentPosition])
                }
                j.join()
                delay(1000)
                flag = ModifyHotelPresenter.flag
                Log.d("HotelBookingFrag","${ModifyHotelPresenter.flag}")
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Are you sure?")
                builder.setMessage("You won't be able to revert the changes")
                pBar.visibility = View.GONE
                builder.setPositiveButton("Yes") { _, _ ->
                    hotelRemoveConditions(flag)
                }
                builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
                val dlg=builder.create()
                dlg.show()
            }
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


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.removeListener()
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        editHotelName.setText(hotelList[position].name)
        editHotelAddress.setText(hotelList[position].address)
        editHotelDesc.setText(hotelList[position].description)
        modifyHotelOffer.setText(hotelList[position].specialOffer)
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    override fun getHotelsStatus(msg: String, hNames: MutableList<String>, hId: MutableList<String>, hList: MutableList<Hotels>) {
        pBar.visibility = View.GONE
        if(msg=="Success")
        {
            hotelNames = hNames
            hotelId =hId
            hotelList =hList

            spinnerAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_dropdown_item, hotelNames)
            spinnerModidyHotel.adapter = spinnerAdapter
        }
        else{
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("No Hotels Added").show()
        }
    }


    override fun modifyHotelStatus(msg: String) {
        pBar.visibility = View.GONE
        if(msg=="Success"){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Hotel Updated").show()
            spinnerAdapter.notifyDataSetChanged()
        }
    }

    override fun removeHotelStatus(msg: String) {
        if(msg=="Success"){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Hotel Removed").show()
//            presenter.getHotelsFireBase()
        }
    }

    override fun hotelRemoveConditions(flag : Int) {
        if(flag==1){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Cannot remove as there are some bookings made for the selected hotel").show()
        }
        else if(flag==2){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Cannot remove as there are some employees working in the hotel").show()
        }
        else{
            presenter.removeHotelFireBase(hotelId[currentPosition])
        }
    }


}