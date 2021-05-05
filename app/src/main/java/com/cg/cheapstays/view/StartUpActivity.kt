package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Users
import com.cg.cheapstays.view.admin.AdminActivity
import com.cg.cheapstays.view.utils.isOnline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_start_up.*
var USER_TYPE = ""
class StartUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        if(!isOnline(this)){
            startActivity(Intent(this,NoInternetActivity::class.java))
            finish()
        }


        supportActionBar?.hide()
        userCV.setOnClickListener{
            USER_TYPE = "user"
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","user")
            startActivity(intent)
            finish()
        }

        hotelCV.setOnClickListener{
            USER_TYPE = "employee"
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","employee")
            startActivity(intent)
            finish()
        }

        startupAdminB.setOnClickListener{
            USER_TYPE = "admin"
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","admin")
            startActivity(intent)
            finish()
        }


    }
}