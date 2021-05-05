package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.SignUpPresenter
import com.cg.cheapstays.view.utils.MakeProgressBar
import com.cg.cheapstays.view.utils.isOnline
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity(),SignUpPresenter.View {

    lateinit var presenter: SignUpPresenter
    lateinit var type : String
    lateinit var hotels: MutableList<String>
    lateinit var hotelId : MutableList<String>
    lateinit var hotelAdapter : ArrayAdapter<String>
    var selectedHotelId : String? =null
    lateinit var pBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        pBar = MakeProgressBar(findViewById(android.R.id.content)).make()
        pBar.visibility = View.GONE

        // Checking if internet connection is there or not
        if(!isOnline(this)){
            startActivity(Intent(this,NoInternetActivity::class.java))
            finish()
        }

        // Initializing presenter and required lists
        presenter = SignUpPresenter(this)
        presenter.initialize()
        type = USER_TYPE
        hotels = mutableListOf<String>()
        hotelId = mutableListOf<String>()

        presenter.getHotelsFireBase()

        // Checking if Email/Password is empty
        signUpBtn.setOnClickListener{
            if(emailE.text.isNullOrEmpty() || passwordE.text.isNullOrEmpty()){
                MakeSnackBar(it).make("Empty Email Id or password").show()
            }
            else{
                if(type=="employee"){
                    // Building a dialog if the user Type is Employee to select their hotel
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Select your hotel".toUpperCase(Locale.ROOT))
                        .setCancelable(false)
                        .setSingleChoiceItems(hotelAdapter,0) { dialog, which ->
                            selectedHotelId = hotelId[which]
                            dialog.dismiss()
                            pBar.visibility = View.VISIBLE
                            doSignUp()
                        }.show()
                }
                else doSignUp()
            }
        }
        existingUserT.setOnClickListener{
            // Sign In Activity if existing user
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }


    }

    private fun doSignUp() {
        presenter.signUpFireBase(nameE.text.toString(),emailE.text.toString(),passwordE.text.toString(),selectedHotelId)
    }

    override fun setHotelAdapter(obj: MutableList<String>, id: MutableList<String>) {
        // Creating adapter for employee's hotel selection
        hotels =obj
        hotelId =id
        hotelAdapter = ArrayAdapter(this@SignUpActivity,android.R.layout.simple_selectable_list_item,hotels)
    }

    override fun signUpStatus(msg: String) {
        pBar.visibility = View.GONE
        // Checking if registration was successful
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