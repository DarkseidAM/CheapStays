package com.cg.cheapstays.view.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.cheapstays.R
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val frag = AdminStartUpFragment()
        supportFragmentManager.beginTransaction().replace(R.id.parentAdmin,frag)
            .commit()

    }

    override fun onResume() {
        super.onResume()

    }
}