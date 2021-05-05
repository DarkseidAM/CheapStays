package com.cg.cheapstays.view.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cg.cheapstays.R
import com.cg.cheapstays.view.StartUpActivity
import com.cg.cheapstays.presenter.admin.AdminStartUpPresenter
import com.cg.cheapstays.view.user.hotels.HotelsFragment
import kotlinx.android.synthetic.main.fragment_admin_start_up.*

class AdminStartUpFragment : Fragment(), AdminStartUpPresenter.View {
    lateinit var presenter: AdminStartUpPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =AdminStartUpPresenter(this)
        presenter.initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_start_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adminAddHotelB.setOnClickListener {
            val frag = AddHotelFragment()
            startAdminFragment(frag)
            startAdminFragment(frag)
        }
        adminModifyHotel.setOnClickListener {
            val frag = ModifyHotelFragment()
            startAdminFragment(frag)
        }
        adminListHotelB.setOnClickListener {
            val frag = HotelsFragment()
            startAdminFragment(frag)
        }
        logoutB.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want  to Logout?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                presenter.signOutFireBase()
                startActivity(Intent(activity,StartUpActivity::class.java))
                activity?.finish()
            })
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
            val dlg=builder.create()
            dlg.show()
        }
        adminBookingListB.setOnClickListener{
            val frag = AdminBookingsFragment()
            startAdminFragment(frag)
        }
    }
    override fun startAdminFragment(frag: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.parentAdmin, frag)
            ?.addToBackStack(null)
            ?.commit()
    }
}