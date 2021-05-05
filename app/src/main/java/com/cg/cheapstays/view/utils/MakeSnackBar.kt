package com.cg.cheapstays.view.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

class MakeSnackBar (view : View) {


    val sb = Snackbar.make(view,"",Snackbar.LENGTH_SHORT)

    fun make(s : String) : Snackbar{
        sb.animationMode = Snackbar.ANIMATION_MODE_FADE
        sb.duration = 4000
        sb.setTextColor(Color.parseColor("#FFFFFF44"))
        sb.setAction("Dismiss"){
            sb.dismiss()
        }
        sb.setText(s)
        sb.setActionTextColor(Color.parseColor("#ffff2222"))
//        sb.setBackgroundTint(Color.parseColor("#ffc0cb"))
        return sb
    }


}