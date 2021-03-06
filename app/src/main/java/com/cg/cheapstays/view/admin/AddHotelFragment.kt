package com.cg.cheapstays.view.admin
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.cg.cheapstays.R
import com.cg.cheapstays.view.utils.MakeSnackBar
import com.cg.cheapstays.presenter.admin.AddHotelPresenter
import com.cg.cheapstays.view.NoInternetActivity
import com.cg.cheapstays.view.utils.MakeProgressBar
import com.cg.cheapstays.view.utils.isOnline
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_hotel.*

class AddHotelFragment : Fragment(),AddHotelPresenter.View {
    lateinit var presenter: AddHotelPresenter
    private var filePath : Uri? = null
    private var imageUrl : Uri? = null
    var imageUploaded = false
    lateinit var pBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!isOnline(activity?.applicationContext!!)){
            startActivity(Intent(activity?.applicationContext!!, NoInternetActivity::class.java))
            activity?.finish()
        }
        // Initialize Presenter
        presenter = AddHotelPresenter(this)
        presenter.initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_hotel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        pBar.visibility = View.GONE
        super.onViewCreated(view, savedInstanceState)
        imageUrl = Uri.EMPTY
        addHotelImage.setOnClickListener {
            // Select Image
            openGalleryForImage()
        }
        uploadButton.setOnClickListener {
            // Upload Image
            uploadImage()
        }
        addHotelBtn.setOnClickListener {
            // Add Hotel
            if(addHotelName.text.isNotEmpty() && addHotelAddress.text.isNotEmpty() && addHotelRatings.text.isNotEmpty() && addHotelDesc.text.isNotEmpty()){
                if (imageUploaded) {
                    pBar.visibility = View.VISIBLE
                    presenter.addHotelFireBase(addHotelName.text.toString(),addHotelAddress.text.toString(),addHotelDesc.text.toString(),0,addHotelRatings.text.toString().toDouble(),imageUrl.toString(),addHotelOffer.text.toString())
                } else {
                    MaterialAlertDialogBuilder(it.context).setTitle("Confirmation")
                        .setMessage("Do you want to continue without adding an image?")
                        .setPositiveButton("Yes"){ _: DialogInterface, _: Int ->
                            pBar.visibility = View.VISIBLE
                            presenter.addHotelFireBase(addHotelName.text.toString(),addHotelAddress.text.toString(),addHotelDesc.text.toString(),0,addHotelRatings.text.toString().toDouble(),imageUrl.toString(),addHotelOffer.text.toString())
                        }.setNegativeButton("No"){ dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }.show()

                }
            }else{
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Please enter all the details").show()
            }
        }
    }

    private fun addRoom(hotelid: String?) {
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
            presenter.imageUploadFireBase(filePath!!)
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
        }
        else{
            MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Error in selecting image").show()

        }
    }

    // Check if image got uploaded
    override fun uploadImageStatus(status: String, progress: Int, result: Uri?) {
        when(status){
            "Start"->{
                progressBar2.visibility = View.VISIBLE
            }
            "Success"->{
                progressBar2.visibility = View.GONE
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Image Uploaded").show()
            }
            "Failure"->{
                progressBar2.visibility = View.GONE
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("Error in uploading image").show()
            }
            "Uploading"->{
                progressBar2.progress = progress
            }
            "Completed"->{
                imageUrl = result
                imageUploaded = true
            }
        }
    }

    // Check hotel creation status
    override fun addHotelStatus(status: String, hotelid: String?, msg: String?) {
        when (status) {
            "Success" -> {
                pBar.visibility = View.GONE
                addRoom(hotelid)
            }
            "Failure" -> {
                MakeSnackBar(activity?.findViewById(android.R.id.content)!!).make("$msg").show()
            }
        }
    }

}