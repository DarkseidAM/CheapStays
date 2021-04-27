package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.cheapstays.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        CoroutineScope(Dispatchers.Main).launch{
            delay(1000)
            startActivity(Intent(this@MainActivity,SignInActivity::class.java))
            finish()
        }

    }
}
