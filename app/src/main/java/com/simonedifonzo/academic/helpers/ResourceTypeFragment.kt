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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User

class ResourceTypeFragment(private var service: GoogleService, private var userData: User) :
    BottomSheetDialogFragment() {

    val DOCUMENT = 0
    val MEDIA = 1

    private lateinit var btnDocument : Button
    private lateinit var btnMedia : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.resourcetype_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDocument = view.findViewById(R.id.btn_document)
        btnDocument.setOnClickListener {
            var intent = Intent()
            intent.type = "pdf/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select DOCUMENT"), DOCUMENT)
        }

        btnMedia = view.findViewById(R.id.btn_media)
        btnMedia.setOnClickListener {
            var intent = Intent()
            intent.type = "docx/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select MEDIA"), MEDIA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            return
        }

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            return
        }

        when (resultCode) {
            DOCUMENT -> {
                upload(data!!.data)
            }

            MEDIA -> {
                upload(data!!.data)
            }
        }
    }

    private fun upload(data: Uri?) {
        TODO("Not yet implemented")
    }

//    private fun uploadImageToFirebase(imageUri: Uri, name: String) {
//
//        val fileRef: StorageReference? =
//            service.storage?.child("users/" + service.auth.uid.toString() + "/" + name)
//
//
//        fileRef?.putFile(imageUri)?.addOnSuccessListener {
//
//            if (name == "profile.jpg") {
//                Toast.makeText(context, "Profile picture uploaded", Toast.LENGTH_SHORT).show()
//
//                userData.profilePic = ("users/" + service.auth.uid.toString() + "/" + name)
//
//                service.firestore
//                    .collection("users")
//                    .document(Objects.requireNonNull(service.auth.uid.toString()))
//                    .update("profilePic", userData.profilePic)
//            }
//        }?.addOnFailureListener {
//            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
//        }
//    }
}