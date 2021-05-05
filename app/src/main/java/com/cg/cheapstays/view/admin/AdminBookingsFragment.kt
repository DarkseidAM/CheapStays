package com.cg.cheapstays.view.admin

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
import android.widget.ProgressBar
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.presenter.admin.AdminBookingsPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.MakeProgressBar
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.view.utils.isOnline

class AdminBookingsFragment : Fragment(), AdminBookingsPresenter.View {
    private var columnCount = 1
    lateinit var presenter : AdminBookingsPresenter
    lateinit var pBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
        // Initialize presenter
        presenter = AdminBookingsPresenter(this)
        presenter.initialize()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.all_booking_list, container, false)
        if (view is RecyclerView) {
            // Initialize Recycler View
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        super.onViewCreated(view, savedInstanceState)
        pBar.visibility = View.VISIBLE
        presenter.getBookingList()
    }

    override fun changeBookingAdapter(bookingMap: MutableMap<Bookings, String>, bookingList: MutableList<Bookings>) {
        Log.d("AdminBookings",bookingList.toString())
        // Setting Recycler View Adapter
        (view as RecyclerView).adapter = MyAdminBookingsRecyclerViewAdapter(bookingList)
        pBar.visibility = View.GONE

    }
}