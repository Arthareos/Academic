package com.simonedifonzo.academic.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.simonedifonzo.academic.CourseActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.*
import java.util.*

class CreateMediaResource : AppCompatActivity() {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()
    private var course: Course = Course()
    private var resource: Resource = Resource()

    private lateinit var resourcesRef: CollectionReference

    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var txtHeader: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var txtActions: TextView

    private lateinit var resourceName: TextView
    private lateinit var resourceDescription: TextView
    private lateinit var resourceLink: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_media_resource)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User
        course = intent.getSerializableExtra("course") as Course

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
        btnBack = findViewById(R.id.back_button)
        txtHeader = findViewById(R.id.header_title)
        txtSubtitle = findViewById(R.id.header_subtitle)
        txtActions = findViewById(R.id.txt_actions)
        btnAdd = findViewById(R.id.button_create)

        resourceName = findViewById(R.id.text_name)
        resourceDescription = findViewById(R.id.text_description)
        resourceLink = findViewById(R.id.text_link)
    }

    private fun initInfo() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtSubtitle.text = course.name

        btnAdd.setOnClickListener {

            if (resourceName.text.isEmpty()) {
                resourceName.error = "Resource name is required"
                return@setOnClickListener
            }

            if (resourceDescription.text.isEmpty()) {
                resourceDescription.error = "Resource description is required"
                return@setOnClickListener
            }

            if (resourceLink.text.isEmpty()) {
                resourceLink.error = "Resource link is required"
                return@setOnClickListener
            }

            resourcesRef.add(resource).addOnCompleteListener {
                if (it.isSuccessful) {
                    resource.id = it.result?.id.toString()

                    resourcesRef.document(resource.id).update("id", resource.id)
                    resourcesRef.document(resource.id).update("name", resourceName.text.toString())
                    resourcesRef.document(resource.id)
                        .update("description", resourceDescription.text.toString())
                    resourcesRef.document(resource.id).update("type", "web")
                    resourcesRef.document(resource.id).update("link", resourceLink.text.toString())
                    resourcesRef.document(resource.id)
                        .update("uploaderID", service.auth.uid.toString())
                    resourcesRef.document(resource.id)
                        .update("uploadedTime", Utils.currentTimeStamp)

                    val intent = Intent(this, CourseActivity::class.java)

                    val bundle = Bundle()
                    bundle.putSerializable("user", userData)
                    bundle.putSerializable("course", course)
                    intent.putExtras(bundle)

                    startActivity(intent)

                    return@addOnCompleteListener
                }

                Toast.makeText(this, "A connection error has occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}