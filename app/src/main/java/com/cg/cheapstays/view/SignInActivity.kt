package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    lateinit var googleSignInClient : GoogleSignInClient
    lateinit var fAuth : FirebaseAuth
    lateinit var fDatabase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleLoginBtn.setOnClickListener {
            googleSignIn()
        }
        signInBtn.setOnClickListener {
            emailSignIn()
        }

    }

    private fun emailSignIn() {
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

        //----LOGIN USER---
        fAuth.signInWithEmailAndPassword(emailLoginE.text.toString(), passwordLoginE.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = fAuth.currentUser
                        updateUI(user)
                    } else { //wrong details
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }

    }

    private fun updateUI(user: FirebaseUser?) { //use this to move to activity
        Toast.makeText(this,"Login successful", Toast.LENGTH_LONG).show()
    }



    private val RC_SIGN_IN : Int = 12

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
                Log.d("Login", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login", "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithCredential:success")
                    val user = fAuth.currentUser
                    val users = Users(user?.displayName!!,user.email!!)
                    fDatabase.reference.child("users").child(user.uid).setValue(users)
                    startActivity(Intent(this@SignInActivity,UserActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Login", "signInWithCredential:failure", task.exception)
                }
            }
    }
}