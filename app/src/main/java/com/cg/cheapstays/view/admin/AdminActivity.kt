package com.cg.cheapstays.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.cheapstays.R
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.isOnline

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        if(!isOnline(this)){
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }

        val frag = AdminStartUpFragment()
        supportFragmentManager.beginTransaction().replace(R.id.parentAdmin,frag)
            .commit()

    }

    override fun onResume() {
        super.onResume()

    }
}