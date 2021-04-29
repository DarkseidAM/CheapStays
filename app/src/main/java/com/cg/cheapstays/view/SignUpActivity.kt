package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {

    lateinit var fDatabase : FirebaseDatabase
    lateinit var fAuth : FirebaseAuth
    lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()

        type = USER_TYPE



        signUpBtn.setOnClickListener{
            if(emailE.text.isNullOrEmpty() || passwordE.text.isNullOrEmpty()){
                Toast.makeText(this,"Empty Email Id or password ",Toast.LENGTH_LONG).show()
            }
            else
                doSignUp()
        }
        existingUserT.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }



    }

    private fun doSignUp() {
        fAuth.createUserWithEmailAndPassword(emailE.text.toString(),passwordE.text.toString()).addOnCompleteListener{
            if(it.isSuccessful){
                val users = Users(nameE.text.toString(),emailE.text.toString(),type)
                val id = it.result?.user?.uid
                fDatabase.reference.child("users").child(id!!).setValue(users)
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