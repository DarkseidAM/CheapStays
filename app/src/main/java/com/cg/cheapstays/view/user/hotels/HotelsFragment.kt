package com.cg.cheapstays.view.user.hotels

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.presenter.user.hotels.HotelsPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.admin.AdminReportActivity
import com.cg.cheapstays.view.utils.MakeProgressBar
import com.cg.cheapstays.view.utils.isOnline

import java.util.*

class HotelsFragment : Fragment() , HotelsPresenter.View{
    lateinit var  presenter: HotelsPresenter
    lateinit var hotelList : MutableList<Hotels>
    lateinit var filteredList : MutableList<Hotels>
    lateinit var hotelMap : MutableMap<Hotels,String>
    lateinit var adapter: MyHotelsRecyclerViewAdapter
    lateinit var pBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
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
        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        pBar.visibility = View.VISIBLE

        presenter.getHotelList()
        // Implementing search for hotels
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
                return false
            }
        })
    }

    // Changing the adapter accordingly on the basis of search results
    override fun changeHotelAdapter(hotels: MutableList<Hotels>, ids: MutableMap<Hotels, String>) {
        hotelList = hotels
        hotelMap = ids
        if(view is RecyclerView){
            pBar.visibility = View.GONE
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