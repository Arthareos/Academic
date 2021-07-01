package com.simonedifonzo.academic.helpers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.simonedifonzo.academic.AcademicWorkBrowserActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import com.simonedifonzo.academic.classes.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class CreateAcademicWorkActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var academicRef: CollectionReference
    private var resourceUri: Uri = Uri.parse("null")

    private lateinit var mainLayout: CoordinatorLayout
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var txtHeader: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var txtActions: TextView

    private lateinit var workName: TextView
    private lateinit var workAuthor: TextView
    private lateinit var workType: Spinner
    private lateinit var btnResource: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_academic_work)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        academicRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection("academic")

        initInfo()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.main_layout)
        btnBack = findViewById(R.id.back_button)
        txtHeader = findViewById(R.id.header_title)
        txtSubtitle = findViewById(R.id.header_subtitle)
        txtActions = findViewById(R.id.txt_actions)
        btnAdd = findViewById(R.id.button_create)

        workName = findViewById(R.id.text_name)
        workAuthor = findViewById(R.id.text_author)
        workType = findViewById(R.id.text_type)
        btnResource = findViewById(R.id.btn_selectresource)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK && requestCode == 86) {
            resourceUri = data.data!!

            btnResource.isEnabled = false
            btnResource.isClickable = false
        } else {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectDocument()
        } else
            Toast.makeText(
                this,
                "Please provide storage permission to continue",
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun initInfo() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtSubtitle.text = (userData.specialization.university
                + " // "
                + userData.specialization.faculty
                + " // "
                + "academic")

        var workLayers = arrayOf("Bachelor", "Master", "Doctorate")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workLayers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        workType.adapter = adapter
        workType.setSelection(0)
        adapter.notifyDataSetChanged()

        btnResource.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectDocument()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    9
                )
            }

        }

        btnAdd.setOnClickListener {

            if (workName.text.isEmpty()) {
                workName.error = "This is a required field"
                return@setOnClickListener
            }

            if (workAuthor.text.isEmpty()) {
                workAuthor.error = "This is a required field"
                return@setOnClickListener
            }

            val academic: MutableMap<String, Any> = HashMap()

            academic["name"] = workName.text.toString()
            academic["author"] = workAuthor.text.toString()
            academic["type"] = workType.selectedItem.toString()

            academicRef.add(academic).addOnSuccessListener {
                GlobalScope.launch(Dispatchers.IO) {
                    async {
                        uploadPdfToFirebase(resourceUri, it.id)
                    }.join()

                    async {
                        academicRef.document(it.id).update("id", it.id)
                        academicRef.document(it.id)
                            .update("uploaderID", service.auth.uid.toString())
                        academicRef.document(it.id).update("uploadedTime", Utils.currentTimeStamp)
                    }.join()
                }

                val intent = Intent(this, AcademicWorkBrowserActivity::class.java)

                val bundle = Bundle()
                bundle.putSerializable("user", userData)
                intent.putExtras(bundle)

                startActivity(intent)

                return@addOnSuccessListener
            }
        }
    }

    private fun selectDocument() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 86)
    }

    private fun uploadPdfToFirebase(fileUri: Uri, id: String) {

        val path = ("resources/"
                + userData.specialization.university
                + "/" + userData.specialization.faculty
                + "/academic"
                + "/" + id
                + "/" + workName.text.toString() + ".pdf")

        val fileRef: StorageReference? = service.storage?.child(path)

        fileRef?.putFile(fileUri)?.addOnCompleteListener {
            if (it.isSuccessful) {
                academicRef.document(id).update("link", path)
            }
        }?.addOnFailureListener {
            Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}