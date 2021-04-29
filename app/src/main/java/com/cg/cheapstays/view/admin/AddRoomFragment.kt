package com.cg.cheapstays.view.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_room.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRoomFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var hotelid : String
    lateinit var fDatabase: FirebaseDatabase
    lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            hotelid = it.getString("hotelid")!!
        }
        fDatabase = FirebaseDatabase.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        ref = fDatabase.reference.child("hotels").child(hotelid).child("rooms")

        return inflater.inflate(R.layout.fragment_add_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomTypeAdapter = ArrayAdapter<String>(activity as Context,android.R.layout.simple_spinner_item, arrayOf("Single","Double"))
        val roomNoAdapter = ArrayAdapter<Int>(activity as Context,android.R.layout.simple_spinner_item, arrayOf(1,2,3,4,5))
        addRoomType.adapter = roomTypeAdapter
        addNoOfBeds.adapter = roomNoAdapter
        var price = Double.MAX_VALUE

        addRoomBtn.setOnClickListener {
            if(roomPrice.text.isNotEmpty()){
                for(i in 1..addNoOfBeds.selectedItem.toString().toInt()){
                    val room = Hotels.Rooms(roomPrice.text.toString().toDouble(), addRoomType.selectedItem.toString())
                    ref.child(UUID.randomUUID().toString()).setValue(room)
                    Log.d("Rooms","$ref")
                    price = Math.min(price,roomPrice.text.toString().toDouble())
                }
                ref.parent?.child("price")?.setValue(price)
                roomReturnBtn.visibility = View.VISIBLE
            }
        }
        roomReturnBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to return to main screen?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->

                startActivity(Intent(activity,AdminActivity::class.java))
                activity?.finish()

            })
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}