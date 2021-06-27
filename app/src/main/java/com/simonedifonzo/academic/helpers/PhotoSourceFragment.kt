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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*")).cropSquare().start()
        }

        btnCamera = view.findViewById(R.id.btn_camera)
        btnCamera.setOnClickListener {
            ImagePicker.with(this).cameraOnly().cropSquare().start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            return
        }

        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            data.data?.let { uploadImageToFirebase(it, "profile.jpg") }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, name: String) {

        val fileRef: StorageReference? =
            service.storage?.child("users/" + service.auth.uid.toString() + "/" + name)


        fileRef?.putFile(imageUri)?.addOnSuccessListener {

            if (name == "profile.jpg") {
                Toast.makeText(context, "Profile picture uploaded", Toast.LENGTH_SHORT).show()

                userData.profilePic = ("users/" + service.auth.uid.toString() + "/" + name)

                service.firestore
                    .collection("users")
                    .document(Objects.requireNonNull(service.auth.uid.toString()))
                    .update("profilePic", userData.profilePic)
            }
        }?.addOnFailureListener {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}