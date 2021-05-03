package com.cg.cheapstays.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.model.Users
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {

    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var type : String
    lateinit var hotels: MutableList<String>
    lateinit var hotelId : MutableList<String>
    lateinit var hotelAdapter : ArrayAdapter<String>
    lateinit var selectedHotelId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()

        type = USER_TYPE
        hotels = mutableListOf<String>()
        hotelId = mutableListOf<String>()

        fDatabase.reference.child("hotels").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(childs in snapshot.children){
                        hotels.add(childs.child("name").value.toString())
                        hotelId.add(childs.key!!)
                    }
                }
                hotelAdapter = ArrayAdapter(this@SignUpActivity,android.R.layout.simple_selectable_list_item,hotels)
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }

        })

        signUpBtn.setOnClickListener{
            if(emailE.text.isNullOrEmpty() || passwordE.text.isNullOrEmpty()){
                Toast.makeText(this,"Empty Email Id or password ",Toast.LENGTH_LONG).show()
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
                else    doSignUp()
            }
        }
        existingUserT.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }



    }

    private fun doSignUp() {
        fAuth.createUserWithEmailAndPassword(emailE.text.toString(),passwordE.text.toString()).addOnCompleteListener{
            if(it.isSuccessful){
                val users = Users(nameE.text.toString(),emailE.text.toString(),type,"")
                val id = it.result?.user?.uid
                fDatabase.reference.child("users").child(id!!).setValue(users)
                if(type=="employee")    fDatabase.reference.child("users").child(id).child("hotelId").setValue(selectedHotelId)
                Toast.makeText(this,"Registration Successful",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,SignInActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this,"${it.exception?.message}",Toast.LENGTH_LONG).show()
            }
        }
    }
}