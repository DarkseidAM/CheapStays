package com.cg.cheapstays.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.admin.AdminReportActivity
import com.cg.cheapstays.view.ui.home.presenter.HotelsPresenter

import java.util.*

class HotelsFragment : Fragment() , HotelsPresenter.View{
    lateinit var  presenter: HotelsPresenter
    lateinit var hotelList : MutableList<Hotels>
    lateinit var filteredList : MutableList<Hotels>
    lateinit var hotelMap : MutableMap<Hotels,String>
    lateinit var adapter: MyHotelsRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = HotelsPresenter(this)
        presenter.initialize()
        hotelList = mutableListOf()
        hotelMap = mutableMapOf()
        filteredList = mutableListOf<Hotels>()
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
        presenter.getHotelList()
        val sv = activity?.findViewById<SearchView>(R.id.userSearchView)
        sv?.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
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

    override fun changeHotelAdapter(hotels: MutableList<Hotels>, ids: MutableMap<Hotels, String>) {
        hotelList = hotels
        hotelMap = ids
        if(view is RecyclerView){
            Log.d("List","List - $hotelList")
            adapter = MyHotelsRecyclerViewAdapter(hotelList){
                if(USER_TYPE!="admin"){
                    val bundle = Bundle()
                    bundle.putString("hotelid",hotelMap[it])
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
                    intent.putExtra("hotelid",hotelMap[it])
                    startActivity(intent)
                }
            }
            (view as RecyclerView).adapter = adapter
        }
    }
}