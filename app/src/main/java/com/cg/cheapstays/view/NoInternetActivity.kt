package com.cg.cheapstays.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.cheapstays.R
import kotlinx.android.synthetic.main.activity_no_internet.*
import kotlin.system.exitProcess

class NoInternetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        exitB.setOnClickListener{
            finish()
            exitProcess(0)
        }
        restartAppB.setOnClickListener {
            val packageMgr = packageManager
            val intent = packageMgr.getLaunchIntentForPackage(packageName)
            val compName = intent?.component
            val mainIntent = Intent.makeRestartActivityTask(compName)
            startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }

    }
}