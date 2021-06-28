package com.simonedifonzo.academic.helpers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.simonedifonzo.academic.CourseActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class CreateDocumentResource : AppCompatActivity() {

    private var service: GoogleService  = GoogleService()
    private var userData: User          = User()
    private var course: Course          = Course()
    private var resource: Resource      = Resource()

    private lateinit var resourcesRef: CollectionReference
    private var resourceUri: Uri = Uri.parse("null")

    private lateinit var btnAdd : FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var txtHeader: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var txtActions: TextView

    private lateinit var resourceName: TextView
    private lateinit var resourceDescription: TextView
    private lateinit var btnSelectResource: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_document_resource)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData    = intent.getSerializableExtra("user") as User
        course      = intent.getSerializableExtra("course") as Course

        resourcesRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection(userData.specialization.year)
            .document(course.id)
            .collection("resources")

        initInfo()
    }

    private fun initViews() {
        btnBack             = findViewById(R.id.back_button)
        txtHeader           = findViewById(R.id.header_title)
        txtSubtitle         = findViewById(R.id.header_subtitle)
        txtActions          = findViewById(R.id.txt_actions)
        btnAdd              = findViewById(R.id.button_create)

        resourceName        = findViewById(R.id.text_name)
        resourceDescription = findViewById(R.id.text_description)
        btnSelectResource   = findViewById(R.id.btn_selectresource)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK && requestCode == 86) {
            resourceUri = data.data!!

            btnSelectResource.isEnabled = false
            btnSelectResource.isClickable = false
        } else {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectDocument()
        } else
            Toast.makeText(this, "Please provide storage permission to continue", Toast.LENGTH_SHORT).show()
    }

    private fun initInfo () {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtSubtitle.text = course.name

        btnSelectResource.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectDocument()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 9)
            }

        }

        btnAdd.setOnClickListener {

            if (resourceName.text.isEmpty()) {
                resourceName.error = "Resource name is required"
                return@setOnClickListener
            }

            if (resourceDescription.text.isEmpty()) {
                resourceDescription.error = "Resource description is required"
                return@setOnClickListener
            }

            if (resourceUri == null) {
                Toast.makeText(this, "Please select a file before submitting", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resourcesRef.add(resource).addOnCompleteListener {
                if (it.isSuccessful) {
                    resource.id = it.result?.id.toString()

                    GlobalScope.launch(Dispatchers.IO) {
                        async {
                            resourcesRef.document(resource.id).update("id", resource.id)
                            resourcesRef.document(resource.id).update("name", resourceName.text.toString())
                            resourcesRef.document(resource.id).update("description", resourceDescription.text.toString())
                            resourcesRef.document(resource.id).update("type", "pdf")
                            resourcesRef.document(resource.id).update("uploaderID", service.auth.uid.toString())
                            resourcesRef.document(resource.id).update("uploadedTime", Utils.currentTimeStamp)
                        }.join()

                        async {
                            uploadPdfToFirebase(resourceUri)
                        }.join()
                    }



//                    resourcesRef.document(resource.id).update("id", resource.id)
//                    resourcesRef.document(resource.id).update("name", resourceName.text.toString())
//                    resourcesRef.document(resource.id).update("description", resourceDescription.text.toString())
//                    resourcesRef.document(resource.id).update("type", "pdf")
//                    resourcesRef.document(resource.id).update("uploaderID", service.auth.uid.toString())
//                    resourcesRef.document(resource.id).update("uploadedTime", Utils.currentTimeStamp)
//
//                    uploadPdfToFirebase(resourceUri)

                    val intent = Intent(this, CourseActivity::class.java)

                    val bundle = Bundle()
                    bundle.putSerializable("user", userData)
                    bundle.putSerializable("course", course)
                    intent.putExtras(bundle)

                    startActivity(intent)

                    return@addOnCompleteListener
                }
            }
        }
    }

    private fun selectDocument() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 86)
    }

    private fun uploadPdfToFirebase(fileUri: Uri) {

        val path = "resources/" + course.id + "/" + resource.id + "/" + resourceName.text.toString() + ".pdf"

        val fileRef: StorageReference? = service.storage?.child(path)

        fileRef?.putFile(fileUri)?.addOnCompleteListener {
            if (it.isSuccessful) {
                resourcesRef.document(resource.id).update("link", path)
            }
        }?.addOnFailureListener {
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}