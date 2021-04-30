package com.cg.cheapstays.view.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.cg.cheapstays.R
import com.cg.cheapstays.view.StartUpActivity
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.ui.home.HotelsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_admin_start_up.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminStartUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminStartUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var fAuth : FirebaseAuth
    lateinit var fDatabase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fDatabase.reference.child("users").child(fAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                USER_TYPE = snapshot.child("userType").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_start_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = view.context as AppCompatActivity

        adminAddHotelB.setOnClickListener {
            val frag = AddHotelFragment()
            //adminCardView.visibility = View.GONE

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.parentAdmin, frag)
                .addToBackStack(null)
                .commit()
        }
        adminModifyHotel.setOnClickListener {
            val frag = ModifyHotelFragment()

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.parentAdmin, frag)
                    .addToBackStack(null)
                    .commit()
        }
        adminListHotelB.setOnClickListener {
            val frag = HotelsFragment()

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.parentAdmin, frag)
                .addToBackStack(null)
                .commit()
        }
        logoutB.setOnClickListener {
            fAuth.signOut()
            startActivity(Intent(getActivity(),StartUpActivity::class.java))
            getActivity()?.finish()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminStartUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminStartUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}