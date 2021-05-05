package com.cg.cheapstays.view.user.hotels

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cg.cheapstays.R
import com.cg.cheapstays.presenter.user.hotels.HotelBasePresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.utils.isOnline

class HotelBaseFragment : Fragment() ,HotelBasePresenter.View{

    lateinit var presenter: HotelBasePresenter
    lateinit var hotelId : String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        presenter = HotelBasePresenter(this)
        presenter.initialize()


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("userType","dghfgh $USER_TYPE")
        if(USER_TYPE=="employee"){
            presenter.getHotelId()
        }
        else{
            val frag = HotelsFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.parent_home_linear,frag)
                ?.commit()
        }
    }

    // Checking if user is employee then taking him to his respective hotel
    override fun sendId(id: String) {
        hotelId = id
        val frag = SelectedHotelFragment()
        val bundle = Bundle()
        bundle.putString("hotelid",hotelId)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.parent_home_linear,frag)
            ?.commit()
    }

}