package com.cg.cheapstays.presenter


import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class SignIn {
    companion object{
        val RC_SIGN_IN :Int = 12
        lateinit var gso : GoogleSignInOptions
    }

    fun initialize(webId : String){
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webId)
                .requestEmail()
                .build()

    }

    interface View{

        val fAuth : FirebaseAuth
        val fDatabase : FirebaseDatabase
        val googleSignInClient : GoogleSignInClient
        fun emailSignIn()
        fun checkUser(user : FirebaseUser?)
        fun updateUI(user : FirebaseUser?)
        fun googleSignIn()
        fun firebaseAuthWithGoogle(idToken: String)

    }

}