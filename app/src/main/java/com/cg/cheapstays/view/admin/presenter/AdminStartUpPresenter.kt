package com.cg.cheapstays.view.admin.presenter
import androidx.fragment.app.Fragment
import com.cg.cheapstays.view.USER_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class AdminStartUpPresenter(view : View) {
    companion object{
        lateinit var fAuth : FirebaseAuth
        lateinit var fDatabase : FirebaseDatabase
    }
    fun initialize(){
        fAuth = FirebaseAuth.getInstance()
        fDatabase = FirebaseDatabase.getInstance()
        fDatabase.reference.child("users").child(fAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                USER_TYPE = snapshot.child("userType").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun signOutFireBase(){
        fAuth.signOut()
    }
    interface View{
        abstract fun startAdminFragment(frag: Fragment)
    }
}