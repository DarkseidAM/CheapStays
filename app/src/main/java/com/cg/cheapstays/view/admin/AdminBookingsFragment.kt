package com.cg.cheapstays.view.admin

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
import com.cg.cheapstays.model.Bookings
import com.cg.cheapstays.presenter.admin.AdminBookingsPresenter

class AdminBookingsFragment : Fragment(), AdminBookingsPresenter.View {
    private var columnCount = 1
    lateinit var presenter : AdminBookingsPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AdminBookingsPresenter(this)
        presenter.initialize()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.all_booking_list, container, false)
        if (view is RecyclerView) {
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
        super.onViewCreated(view, savedInstanceState)
        presenter.getBookingList()
    }

    override fun changeBookingAdapter(bookingMap: MutableMap<Bookings, String>, bookingList: MutableList<Bookings>) {
        Log.d("AdminBookings",bookingList.toString())
        (view as RecyclerView).adapter = MyAdminBookingsRecyclerViewAdapter(bookingList)
    }
}