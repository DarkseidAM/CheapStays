package com.cg.cheapstays.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Users
import com.cg.cheapstays.presenter.SignIn
import com.cg.cheapstays.presenter.SignIn.Companion.RC_SIGN_IN
import com.cg.cheapstays.presenter.SignIn.Companion.gso
import com.cg.cheapstays.view.admin.AdminActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), SignIn.View {

    override lateinit var googleSignInClient : GoogleSignInClient
    override lateinit var fAuth : FirebaseAuth
    override lateinit var fDatabase : FirebaseDatabase
    lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        progressBar.visibility = View.GONE

        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()

        type = USER_TYPE
        Log.d("Login"," $type")
        if(type == "admin"){
            Log.d("Login"," $type")
            NewUserT.visibility = View.GONE
            googleLoginBtn.visibility = View.GONE
        }



        SignIn().initialize(getString(R.string.default_web_client_id))


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
            progressBar.visibility = View.VISIBLE
            emailSignIn()
        }

    }

    override fun onResume() {
        super.onResume()
        if(type!="admin") {
            NewUserT.visibility = View.VISIBLE
            googleLoginBtn.visibility = View.VISIBLE
        }
    }

    override fun  emailSignIn(){
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
                        checkUser(user)
                    } else { //wrong details
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }

    }

    override fun checkUser(user : FirebaseUser?){
        val ref =  fDatabase.reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    val user_type = snapshot.child(user?.uid!!).getValue(Users::class.java)?.userType
                    if (user_type == type) updateUI(user)
                    else {
                        Toast.makeText(this@SignInActivity,"You are not a $type",Toast.LENGTH_LONG).show()
                        fAuth.signOut()
                        startActivity(Intent(this@SignInActivity,StartUpActivity::class.java))
                        finish()
                    }
                }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }

    override fun updateUI(user: FirebaseUser?) { //use this to move to activity
        Toast.makeText(this,"Login successful", Toast.LENGTH_LONG).show()
        if(type=="admin"){
            startActivity(Intent(this@SignInActivity,AdminActivity::class.java))
        }else if(type=="employee"){
            // TODO employee hotel selection
        }else
            startActivity(Intent(this@SignInActivity,UserActivity::class.java))
        finish()

    }


    override fun googleSignIn() {
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
    override fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithCredential:success")
                    val user = fAuth.currentUser
                    val users = Users(user?.displayName!!,user.email!!,type,"")
                    fDatabase.reference.child("users").child(user.uid).setValue(users)
                    updateUI(user)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Login", "signInWithCredential:failure", task.exception)
                }
            }
    }
}