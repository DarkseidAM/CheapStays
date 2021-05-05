package com.cg.cheapstays.presenter.admin
import android.net.Uri
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
class AddHotelPresenter(val view : View) {
    companion object{
        lateinit var fDatabase : FirebaseDatabase
        lateinit var fStorage : FirebaseStorage
        lateinit var storageRef : StorageReference
    }

    // Initialize Firebase
    fun initialize() {
        fDatabase = FirebaseDatabase.getInstance()
        fStorage = FirebaseStorage.getInstance()
        storageRef = fStorage.reference
    }

    // Uploading image on Firebase Storage
    fun imageUploadFireBase(filePath : Uri){
        view.uploadImageStatus("Start")
        val ref: StorageReference =storageRef.child("images/"
                + UUID.randomUUID().toString())
        val uploadTask = ref.putFile(filePath)
        uploadTask.addOnSuccessListener {
            view.uploadImageStatus("Success")
        }.addOnFailureListener{
            view.uploadImageStatus("Failure")
        }.addOnProgressListener {
            view.uploadImageStatus("Uploading")
        }.continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener{
            if(it.isSuccessful){
                view.uploadImageStatus("Completed",100,it.result)
            }
        }
    }

    // Adding hotel to firebase database
    fun addHotelFireBase(name: String, address: String, description: String, price: Int, rating: Double, imgPath: String, specialOffer: String) {
        val db = fDatabase.reference.child("hotels")
        val hotelid = db.push().key!!
        val hotel = Hotels(name, address, description, price, rating, imgPath, specialOffer)
        db.child(hotelid).setValue(hotel).addOnCompleteListener{
            if(it.isSuccessful) {
                view.addHotelStatus("Success", hotelid)
            }
            else{
                view.addHotelStatus("Failure",null,"${it.exception?.message}")
            }
        }
    }
    interface View {
        fun uploadImageStatus(status: String, progress: Int = 0, result: Uri? =null)
        abstract fun addHotelStatus(status: String, hotelid: String? = null, msg: String?=null)
    }
}