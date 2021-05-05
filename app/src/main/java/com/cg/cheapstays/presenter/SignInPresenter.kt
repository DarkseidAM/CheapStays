package com.cg.cheapstays.presenter


import com.cg.cheapstays.model.Users
import com.cg.cheapstays.view.USER_TYPE
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SignInPresenter(val view : View) {
    companion object{
        val RC_SIGN_IN :Int = 12
        lateinit var gso : GoogleSignInOptions
        lateinit var fAuth : FirebaseAuth
        lateinit var fDatabase : FirebaseDatabase
    }


    fun initialize(webId : String){
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webId)
                .requestEmail()
                .build()
        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()

    }


    fun emailSignInFireBase(email: String, password: String) {
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val user = fAuth.currentUser
                val ref =  fDatabase.reference.child("users").child(user.uid)
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            val userType = snapshot.getValue(Users::class.java)?.userType
                            if (userType == USER_TYPE)
                                view.emailSignInStatus("Success", user)
                            else {
                                fAuth.signOut()
                                view.emailSignInStatus("Wrong", null)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {//error
                        view.emailSignInStatus("ERROR : ${error.message} ", null)
                    }
                })

            } else { //wrong details
                view.emailSignInStatus("Failure", null)
            }
        }
    }


    fun googleSignInFireBase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = fAuth.currentUser
                        val users = Users(user?.displayName!!,user.email!!, USER_TYPE,"")
                        if(task.result?.additionalUserInfo?.isNewUser!!) fDatabase.reference.child("users").child(user.uid).setValue(users)
                        view.googleSignInStatus("Success",user)
                    } else {
                        // If sign in fails, display a message to the user.
                        view.googleSignInStatus("Error : ${task.exception?.message}",null)
                    }
                }
    }

    interface View{

        val googleSignInClient : GoogleSignInClient

        fun emailSignInStatus(msg: String, user: FirebaseUser?)
        fun googleSignInStatus(msg: String, user: FirebaseUser?)

    }

}