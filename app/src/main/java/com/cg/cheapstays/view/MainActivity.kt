package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.MakeSnackBar
import com.cg.cheapstays.model.Users
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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val fAuth = FirebaseAuth.getInstance()
        val fDatabase = FirebaseDatabase.getInstance()
        if(fAuth.currentUser != null){
            MakeSnackBar(findViewById(android.R.id.content)).make("Automatically Signing you in...").show()
            val id = fAuth.currentUser?.uid!!
            val ref =  fDatabase.reference.child("users")
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        runOnUiThread{
                            val user_type = snapshot.child(id).getValue(Users::class.java)?.userType
                            if (user_type == "admin") startActivity(Intent(this@MainActivity,AdminActivity::class.java))
                            else startActivity(Intent(this@MainActivity, UserActivity::class.java))
                            finish()
                    }}

                    override fun onCancelled(error: DatabaseError) {
                        // Do nothing

                    }
                })
            }

        }else{
            CoroutineScope(Dispatchers.Main).launch{
                delay(1000)
                startActivity(Intent(this@MainActivity, StartUpActivity::class.java))
                finish()
            }
        }



    }
}
