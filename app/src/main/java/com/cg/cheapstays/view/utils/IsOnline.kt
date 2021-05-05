package com.cg.cheapstays.view.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

public fun isOnline(context : Context) : Boolean{
    val cm = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo!=null && netInfo.isConnectedOrConnecting
}