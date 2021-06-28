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
import com.google.firebase.firestore.CollectionReference
import com.simonedifonzo.academic.CourseActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.*

class ResourceTypeFragment(private var service: GoogleService, private var userData: User, private var course: Course) :
    BottomSheetDialogFragment() {

    private lateinit var btnDocument : Button
    private lateinit var btnMedia : Button

    private lateinit var resource : Resource
    private lateinit var resourcesRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.resourcetype_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resourcesRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection(userData.specialization.year)
            .document(course.id)
            .collection("resources")

        resource = Resource()
        resource.uploaderID = service.auth.uid.toString()
        resource.uploadedTime = Utils.currentTimeStamp

        btnDocument = view.findViewById(R.id.btn_document)
        btnDocument.setOnClickListener {
            val intent = Intent(context, CreateDocumentResource::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            bundle.putSerializable("course", course)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        btnMedia = view.findViewById(R.id.btn_media)
        btnMedia.setOnClickListener {
            val intent = Intent(context, CreateMediaResource::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            bundle.putSerializable("course", course)
            intent.putExtras(bundle)

            startActivity(intent)
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