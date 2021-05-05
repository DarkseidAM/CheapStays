package com.cg.cheapstays.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cg.cheapstays.R
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.SignInPresenter
import com.cg.cheapstays.presenter.SignInPresenter.Companion.RC_SIGN_IN
import com.cg.cheapstays.presenter.SignInPresenter.Companion.gso
import com.cg.cheapstays.view.admin.AdminActivity
import com.cg.cheapstays.view.user.UserActivity
import com.cg.cheapstays.view.utils.isOnline
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), SignInPresenter.View {

    lateinit var presenter: SignInPresenter
    override lateinit var googleSignInClient : GoogleSignInClient
    lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        if(!isOnline(this)){
            startActivity(Intent(this,NoInternetActivity::class.java))
            finish()
        }


        progressBar.visibility = View.GONE
        presenter = SignInPresenter(this)
        presenter.initialize(getString(R.string.default_web_client_id))

        type = USER_TYPE
        if(type == "admin" || type == "employee"){
            if(type == "admin")    NewUserT.visibility = View.GONE
            googleLoginBtn.visibility = View.GONE
        }

        NewUserT.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleLoginBtn.setOnClickListener {
            googleSignIn()
        }
        signInBtn.setOnClickListener {
            emailSignIn()
        }

    }


    private fun  emailSignIn(){
        if(emailLoginE.text.isEmpty()){
            emailLoginE.error ="Please enter email"
            emailLoginE.requestFocus()
            return
        }
        if(passwordLoginE.text.isEmpty()){
            passwordLoginE.error ="Please enter password"
            passwordLoginE.requestFocus()
            return
        }
        progressBar.visibility = View.VISIBLE
        //----LOGIN USER---
        presenter.emailSignInFireBase(emailLoginE.text.toString(), passwordLoginE.text.toString())
    }


    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                presenter.googleSignInFireBase(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                MakeSnackBar(findViewById(android.R.id.content)).make("Error in signing : ${e.message}").show()
            }
        }
    }


    private fun updateUI(user: FirebaseUser?) { //use this to move to activity
        Toast.makeText(this,"Login successful", Toast.LENGTH_LONG).show()
        if(type=="admin"){
            startActivity(Intent(this@SignInActivity,AdminActivity::class.java))
        }else if(type=="employee"){
            startActivity(Intent(this@SignInActivity, UserActivity::class.java))
        }else
            startActivity(Intent(this@SignInActivity, UserActivity::class.java))
        finish()

    }


    override fun emailSignInStatus(msg: String, user: FirebaseUser?) {
        if(msg=="Success")
        {
            updateUI(user)
        }
        else if(msg=="Wrong")
        {
            Toast.makeText(this@SignInActivity,"You are not a $type",Toast.LENGTH_LONG).show()
            startActivity(Intent(this@SignInActivity,StartUpActivity::class.java))
            finish()
        }
        else if(msg=="Failure"){
            MakeSnackBar(findViewById(android.R.id.content)).make("Authentication Failed. Incorrect Username/Password").show()
            progressBar.visibility = View.GONE
        }
        else{
            MakeSnackBar(findViewById(android.R.id.content)).make(msg).show()
            progressBar.visibility = View.GONE
        }
    }

    override fun googleSignInStatus(msg: String, user: FirebaseUser?) {
        if(msg=="Success")
        {
            updateUI(user)
        }
        else{
            MakeSnackBar(findViewById(android.R.id.content)).make(msg).show()
        }
    }
}