package com.cg.cheapstays.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.admin.AdminReportActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class HotelsFragment : Fragment() {

    private var columnCount = 1
    lateinit var fDatabase: FirebaseDatabase
    lateinit var hotelList : MutableList<Hotels>
    lateinit var filteredList : MutableList<Hotels>
    lateinit var hotelId : MutableList<String>
    lateinit var adapter: MyHotelsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fDatabase = FirebaseDatabase.getInstance()
        hotelList = mutableListOf()
        hotelId = mutableListOf()
        filteredList = mutableListOf<Hotels>()
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        super.onResume()
        filteredList.clear()
        activity?.findViewById<SearchView>(R.id.userSearchView)?.visibility = View.VISIBLE
        activity?.findViewById<SearchView>(R.id.userSearchView)?.setQuery("",false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hotel_lists, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        adapter = MyHotelsRecyclerViewAdapter(hotelList){
                            if(USER_TYPE!="admin"){
                                val bundle = Bundle()
                                if(filteredList.isNotEmpty())
                                    bundle.putString("hotelid",hotelId[hotelList.indexOf(filteredList[it])])
                                else
                                    bundle.putString("hotelid",hotelId[it])
                                val frag = SelectedHotelFragment()
                                frag.arguments = bundle
                                activity?.findViewById<SearchView>(R.id.userSearchView)?.visibility = View.GONE
                                activity?.findViewById<SearchView>(R.id.userSearchView)?.clearFocus()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.remove(HotelsFragment())
                                    ?.replace(R.id.parent_home_linear,frag)
                                    ?.commit()
                            }else{
                                val intent = Intent(activity, AdminReportActivity::class.java)
                                intent.putExtra("hotelid",hotelId[it])
                                startActivity(intent)
                            }

                        }
                        view.adapter = adapter

                    }
                    ref.removeEventListener(this)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })

        val sv = activity?.findViewById<SearchView>(R.id.userSearchView)

        sv?.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //
                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {

                filteredList = hotelList.filter {
                    it.name.toLowerCase(Locale.getDefault()).contains(newText!!)
                }.toMutableList()

                if(filteredList.isNotEmpty())   adapter.filter(filteredList)
                Log.d("Listener","$filteredList")

                return false
            }

        })

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