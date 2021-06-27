package com.simonedifonzo.academic.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import java.util.*

class PhotoSourceFragment(private var service: GoogleService, private var userData: User) :
    BottomSheetDialogFragment() {

    private lateinit var btnGallery : Button
    private lateinit var btnCamera : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.photosource_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGallery = view.findViewById(R.id.btn_gallery)
        btnGallery.setOnClickListener {
            Toast.makeText(context, "You selected gallery", Toast.LENGTH_SHORT).show()
            ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*")).cropSquare().start()
        }

        btnCamera = view.findViewById(R.id.btn_camera)
        btnCamera.setOnClickListener {
            Toast.makeText(context, "You selected camera", Toast.LENGTH_SHORT).show()
            ImagePicker.with(this).cameraOnly().cropSquare().start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {

            if (data != null) {
                data.data?.let { uploadImageToFirebase(it, "profile.jpg") }
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, name: String) {

        val fileRef: StorageReference? =
            service.storage?.child("users/" + service.auth.uid.toString() + "/" + name)


        fileRef?.putFile(imageUri)?.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
            Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()

            userData.profilePic = ("users/" + service.auth.uid.toString() + "/" + name)

            service.firestore
                .collection("users")
                .document(Objects.requireNonNull(service.auth.uid.toString()))
                .update("profilePic", userData.profilePic)


            fileRef.downloadUrl
                .addOnSuccessListener {
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }
        }?.addOnFailureListener {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}