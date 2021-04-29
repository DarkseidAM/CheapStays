package com.cg.cheapstays.view.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_hotel.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddHotelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddHotelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var fDatabase : FirebaseDatabase
    lateinit var fStorage : FirebaseStorage
    lateinit var storageRef : StorageReference
    private var filePath : Uri? = null
    private var imageUrl : Uri? = null
    var imageUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fStorage = FirebaseStorage.getInstance()
        storageRef = fStorage.reference

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fDatabase = FirebaseDatabase.getInstance()
        return inflater.inflate(R.layout.fragment_add_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUrl = Uri.EMPTY

        addHotelImage.setOnClickListener {
            openGalleryForImage()
        }
        uploadButton.setOnClickListener {
            uploadImage()
        }
        addHotelBtn.setOnClickListener {

            if(addHotelName.text.isNotEmpty() && addHotelAddress.text.isNotEmpty() && addHotelRatings.text.isNotEmpty() && addHotelDesc.text.isNotEmpty()){
                val db = fDatabase.reference.child("hotels")
                val hotelid = db.push().key!!

                Log.d("Upload","$imageUrl")
                val hotel = Hotels(addHotelName.text.toString(),addHotelAddress.text.toString(),addHotelDesc.text.toString(),0,addHotelRatings.text.toString().toDouble(),imageUrl.toString(),addHotelOffer.text.toString())
                    listOf<Hotels.Rooms>()
                db.child(hotelid).setValue(hotel).addOnCompleteListener{
                    if(it.isSuccessful){
                        if(imageUploaded){
                            addRoom(hotelid)
                        }else{
                            val builder = AlertDialog.Builder(activity)
                            builder.setTitle("Confirmation")
                            builder.setMessage("Do you want to continue without adding an image?")
                            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                                addRoom(hotelid)
                            })
                            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel()}//trailing lambda
                            val dlg=builder.create()
                            dlg.show()
                        }

                    }
                    else{
                        Toast.makeText(activity,"${it.exception?.message}",Toast.LENGTH_LONG).show()
                    }
                }

            }else{
                Toast.makeText(activity,"Please enter all the details",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addRoom(hotelid: String) {
        Toast.makeText(activity, "Added hotel successfully", Toast.LENGTH_LONG).show()
        val frag = AddRoomFragment()
        val bundle = Bundle()
        bundle.putString("hotelid", hotelid)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.parentAdmin, frag)
                ?.commit()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }
    private fun uploadImage(){
        if (filePath != null) {
            progressBar2.visibility = View.VISIBLE
            val ref: StorageReference =storageRef.child("images/"
                    + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(filePath!!)
            uploadTask.addOnSuccessListener {
                progressBar2.visibility = View.GONE
                Toast.makeText(activity,"Uploaded",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                progressBar2.visibility = View.GONE
                Toast.makeText(activity,"Failed ${it.message}",Toast.LENGTH_LONG).show()
            }.addOnProgressListener {
                progressBar2.progress = (((it.bytesTransferred)/(it.totalByteCount))*100).toInt()
            }.continueWithTask {
                ref.downloadUrl
            }.addOnCompleteListener{
                if(it.isSuccessful)
                    imageUrl = it.result
                Log.d("Upload","$imageUrl")
                imageUploaded = true
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1  && data != null
            && data.data != null){
            selectedImageView.setImageURI(data.data) // handle chosen image
            // Get the Uri of data
            filePath = data.data
            selectedImageView.visibility = View.VISIBLE
            uploadButton.visibility = View.VISIBLE
//            Toast.makeText(activity,filePath.toString(),Toast.LENGTH_LONG).show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddHotelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddHotelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}