package com.cg.cheapstays.view.ui.notifications

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Users
import com.cg.cheapstays.view.StartUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //----START GETTING AUTH---
        val fAuth = FirebaseAuth.getInstance()
        val fDatabase = FirebaseDatabase.getInstance()
        val id = fAuth.currentUser?.uid!!
        val ref =  fDatabase.reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.child(id).getValue(Users::class.java)!!
                settingsNameE.setText(user.name)
                settingsEmailE.setText(user.email)
                settingsPhoneE.setText(user.phone)//TODO add phone to user model
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        settingsConfirmB.setOnClickListener{
            updateUserData(ref.child(id))
        }
        settingsLogoutB.setOnClickListener{
            signOutUser(fAuth)
        }
    }

    private fun signOutUser(fAuth: FirebaseAuth) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmation")
        builder.setMessage("Do you want  to Logout?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            fAuth.signOut()
            startActivity(Intent(activity, StartUpActivity::class.java))
            activity?.finish()
        })
        builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
        builder.setNeutralButton("Cancel") { dialog, _ ->dialog.cancel()}
        val dlg=builder.create()
        dlg.show()
    }

    private fun updateUserData(user: DatabaseReference) {
        //TODO
        Log.d("Settings","${user.toString()}")
        val name = settingsNameE.text.toString()
        val phone =  settingsPhoneE.text.toString()
        if(!name.isNullOrEmpty())
            user.child("name").setValue(name)
        if(!phone.isNullOrEmpty())
            user.child("phone").setValue(phone)
    }
}