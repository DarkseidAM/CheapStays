package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.cheapstays.R
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.MainPresenter
import com.cg.cheapstays.view.admin.AdminActivity
import com.cg.cheapstays.view.user.UserActivity
import com.cg.cheapstays.view.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainPresenter.View {

    lateinit var presenter:MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Checking if internet connection is there or not
        if(!isOnline(this)){
            startActivity(Intent(this,NoInternetActivity::class.java))
            finish()
        }
        else{
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

    }


    override fun checkUserStatus(msg: String, userType: String?) {
        // Automatic Logging in
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
