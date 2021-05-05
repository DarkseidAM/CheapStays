package com.cg.cheapstays.view.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cg.cheapstays.R
import com.cg.cheapstays.presenter.UserPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.USER_TYPE
import com.cg.cheapstays.view.utils.isOnline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserActivity : AppCompatActivity(),UserPresenter.View {

    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var  presenter: UserPresenter
    lateinit var navView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isOnline(this)){
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }

        presenter= UserPresenter(this)
        presenter.initialize()

        presenter.setUserTypeFireBase()
//        setContentView(R.layout.activity_user)


    }

    override fun userTypeStatus(msg: String) {
        if(msg == "SUCCESS"){
            Log.d("userType","dghfgh $USER_TYPE")
            setContentView(R.layout.activity_user)
            navView = findViewById(R.id.nav_view)
            val navController = findNavController(R.id.nav_host_fragment)
            navView.setupWithNavController(navController)
            navView.setOnNavigationItemReselectedListener {   }

        }
    }
}