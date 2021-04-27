package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import com.cg.cheapstays.R
import kotlinx.android.synthetic.main.activity_start_up.*

class StartUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        supportActionBar?.hide()
        // TODO to implement automatic sign in
        userCV.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","user")
            startActivity(intent)
            finish()
        }

        hotelCV.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","employee")
            startActivity(intent)
            finish()
        }

        startupAdminB.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            intent.putExtra("type","admin")
            startActivity(intent)
            finish()
        }


    }
}