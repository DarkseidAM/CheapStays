package com.cg.cheapstays.view

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cg.cheapstays.R
import com.cg.cheapstays.presenter.UserPresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserActivity : AppCompatActivity(),UserPresenter.View {

    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var  presenter: UserPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        presenter= UserPresenter(this)
        presenter.initialize()

        presenter.setUserTypeFireBase()
        /*fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()

        fDatabase.reference.child("users").child(fAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                USER_TYPE = snapshot.child("userType").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })*/

        navView.setOnNavigationItemReselectedListener {   }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
}