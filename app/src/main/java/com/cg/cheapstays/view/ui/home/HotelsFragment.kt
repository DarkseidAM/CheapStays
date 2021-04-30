package com.cg.cheapstays.view.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A fragment representing a list of Items.
 */
class HotelsFragment : Fragment() {

    private var columnCount = 1
    lateinit var fDatabase: FirebaseDatabase
    lateinit var hotelList : MutableList<Hotels>
    lateinit var hotelId : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fDatabase = FirebaseDatabase.getInstance()
        hotelList = mutableListOf()
        hotelId = mutableListOf()
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hotel_lists, container, false)

        val ref = fDatabase.reference.child("hotels")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    hotelList.clear()
                    hotelId.clear()
                    for(child in snapshot.children){
                        val hotel = child.getValue(Hotels::class.java)
                        hotelList.add(hotel!!)
                        hotelId.add(child.key.toString())
//                        Log.d("List","List - $hotelList,$hotel")
                    }
                    //Sets adapter
                    if(view is RecyclerView){
                        Log.d("List","List - $hotelList")
                        view.adapter = MyHotelsRecyclerViewAdapter(hotelList){
                            val bundle = Bundle()
                            bundle.putString("hotelid",hotelId[it])
                            val frag = SelectedHotelFragment()
                            frag.arguments = bundle
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.remove(HotelsFragment())
                                ?.replace(R.id.parent_home_linear,frag)
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })



        return view
    }



    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                HotelsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}