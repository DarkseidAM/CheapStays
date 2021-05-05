package com.cg.cheapstays.view.user.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cg.cheapstays.R

class BookingsDashboardFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val frag = BookingsFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.bookingsParentL,frag)
            ?.commit()

        return root
    }

}