package com.cg.cheapstays.view.user

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cg.cheapstays.R
import com.cg.cheapstays.presenter.UserPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.isOnline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserActivity : AppCompatActivity(),UserPresenter.View {

    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var  presenter: UserPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(this)){
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_user)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        presenter= UserPresenter(this)
        presenter.initialize()

        presenter.setUserTypeFireBase()

        navView.setOnNavigationItemReselectedListener {   }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
}