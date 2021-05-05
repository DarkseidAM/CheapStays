package com.cg.cheapstays.view.user.hotels

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cg.cheapstays.R
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.isOnline

class HotelBaseFragment : Fragment() {


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

        val frag = HotelsFragment()
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.parent_home_linear,frag)
                ?.commit()
        return root
    }

}