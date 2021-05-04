package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.MakeSnackBar
import com.cg.cheapstays.model.Users
import com.cg.cheapstays.presenter.MainPresenter
import com.cg.cheapstays.view.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainPresenter.View {

    lateinit var presenter:MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        presenter = MainPresenter(this)
        presenter.initialize()

        if(MainPresenter.fAuth.currentUser != null){
            MakeSnackBar(findViewById(android.R.id.content)).make("Automatically Signing you in...").show()
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                presenter.checkUserFireBase()
            }
        }else{
            //---SPLASH SCREEN---
            CoroutineScope(Dispatchers.Main).launch{
                delay(1000)
                startActivity(Intent(this@MainActivity, StartUpActivity::class.java))
                finish()
            }
        }
    }


    override fun checkUserStatus(msg: String, userType: String?) {
        if(msg!="Success"){
            MakeSnackBar(findViewById(android.R.id.content)).make(msg).show()
        }
        else {
            if (userType == "admin") startActivity(Intent(this@MainActivity, AdminActivity::class.java))
            else startActivity(Intent(this@MainActivity, UserActivity::class.java))
            finish()
        }
    }

}
