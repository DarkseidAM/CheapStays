package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.SignUpPresenter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(),SignUpPresenter.View {

    lateinit var presenter: SignUpPresenter
    lateinit var type : String
    lateinit var hotels: MutableList<String>
    lateinit var hotelId : MutableList<String>
    lateinit var hotelAdapter : ArrayAdapter<String>
    var selectedHotelId : String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        presenter = SignUpPresenter(this)
        presenter.initialize()
        type = USER_TYPE
        hotels = mutableListOf<String>()
        hotelId = mutableListOf<String>()

        presenter.getHotelsFireBase()

        signUpBtn.setOnClickListener{
            if(emailE.text.isNullOrEmpty() || passwordE.text.isNullOrEmpty()){
                MakeSnackBar(it).make("Empty Email Id or password").show()
            }
            else{
                if(type=="employee"){
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Select your hotel".toUpperCase())
                        .setCancelable(false)
                        .setSingleChoiceItems(hotelAdapter,0) { dialog, which ->
                            selectedHotelId = hotelId[which]
                            dialog.dismiss()
                            doSignUp()
                        }.show()
                }
                else doSignUp()
            }
        }
        existingUserT.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }


    }

    private fun doSignUp() {
        presenter.signUpFireBase(nameE.text.toString(),emailE.text.toString(),passwordE.text.toString(),selectedHotelId)
    }

    override fun setHotelAdapter(obj: MutableList<String>, id: MutableList<String>) {
        hotels =obj
        hotelId =id
        hotelAdapter = ArrayAdapter(this@SignUpActivity,android.R.layout.simple_selectable_list_item,hotels)
    }

    override fun signUpStatus(msg: String) {
        if(msg=="Success"){
            Toast.makeText(this,"Registration Successful", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        else{
            Toast.makeText(this, msg,Toast.LENGTH_LONG).show()
        }
    }
}