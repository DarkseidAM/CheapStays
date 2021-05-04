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
import com.cg.cheapstays.view.admin.presenter.ModifyHotelPresenter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_modify_hotel.*



class ModifyHotelFragment : Fragment(),
        ModifyHotelPresenter.View,
        AdapterView.OnItemSelectedListener {

    lateinit var presenter: ModifyHotelPresenter
    //TODO
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

        presenter = ModifyHotelPresenter(this)
        presenter.initialize()
        //TODO
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

        presenter.getHotelsFireBase()
        spinnerModidyHotel.onItemSelectedListener = this


        editHotelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you confirm the changes")
            builder.setPositiveButton("Yes") { _, _ ->

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
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Are you sure?")
            builder.setMessage("You won't be able to revert the changes")
            builder.setPositiveButton("Yes") { _, _ ->
                presenter.removeHotelFireBase(hotelId[currentPosition])
                //TODO
//                dRef.child(hotelId[currentPosition]).removeValue()
            }
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
        //TODO FIX HOTEL LIST DATA
        if(msg=="Success"){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Hotel Updated").show()
            spinnerAdapter.notifyDataSetChanged()
        }
    }

    override fun removeHotelStatus(msg: String) {
        if(msg=="Success"){
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Hotel Removed").show()
            //TODO FIX HOTEL SPINNER
//            presenter.getHotelsFireBase()
        }
    }


}