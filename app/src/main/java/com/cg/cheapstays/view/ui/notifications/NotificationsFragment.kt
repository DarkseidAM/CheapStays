package com.cg.cheapstays.view.ui.notifications

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cg.cheapstays.R
import com.cg.cheapstays.view.StartUpActivity
import com.cg.cheapstays.view.admin.AdminReportActivity
import com.cg.cheapstays.view.ui.notifications.presenter.NotificationPresenter
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment(),NotificationPresenter.View {
    lateinit var presenter: NotificationPresenter
    lateinit var hotelid:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = NotificationPresenter(this)
        settingsEmployeeReportB.visibility = View.INVISIBLE
        //----START GETTING AUTH---
        presenter.initialize()
        presenter.getUserFireBase()
        settingsConfirmB.setOnClickListener{
            updateUserData()
        }
        settingsLogoutB.setOnClickListener{
            signOutUser()
        }
        settingsEmployeeReportB.setOnClickListener{
            startReportActivity()
        }
    }

    private fun signOutUser() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmation")
        builder.setMessage("Do you want  to Logout?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            presenter.signOutUser()
            startActivity(Intent(activity, StartUpActivity::class.java))
            activity?.finish()
        })
        builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
        val dlg=builder.create()
        dlg.show()
    }

    private fun updateUserData() {
        val name = settingsNameE.text.toString()
        val phone =  settingsPhoneE.text.toString()
        presenter.updateUserFireBase(name,phone)
    }

    private fun startReportActivity(){
        val intent = Intent(activity,AdminReportActivity::class.java)
        intent.putExtra("hotelid",hotelid)
        startActivity(intent)
    }

    override fun getUserStatus(name: String, email: String, phone: String, id: String?) {
        if(id!=null){
            settingsEmployeeReportB.visibility = View.VISIBLE
            hotelid=id
        }
        settingsNameE.setText(name)
        settingsEmailE.setText(email)
        settingsPhoneE.setText(phone)
    }
}

