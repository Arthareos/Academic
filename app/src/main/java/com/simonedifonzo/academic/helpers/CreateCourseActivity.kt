package com.simonedifonzo.academic.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.simonedifonzo.academic.CourseBrowserActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.*
import java.util.*

class CreateCourseActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var yearRef: CollectionReference

    private lateinit var mainLayout: CoordinatorLayout
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var txtHeader: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var txtActions: TextView

    private lateinit var courseName: TextView
    private lateinit var courseProfessor: TextView
    private lateinit var courseID: TextView
    private lateinit var courseSemester: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_course)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        yearRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection(userData.specialization.year)

        initInfo()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.main_layout)
        btnBack = findViewById(R.id.back_button)
        txtHeader = findViewById(R.id.header_title)
        txtSubtitle = findViewById(R.id.header_subtitle)
        txtActions = findViewById(R.id.txt_actions)
        btnAdd = findViewById(R.id.button_create)

        courseName = findViewById(R.id.text_name)
        courseProfessor = findViewById(R.id.text_professor)
        courseID = findViewById(R.id.text_id)
        courseSemester = findViewById(R.id.text_semester)
    }

    private fun initInfo() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtSubtitle.text = (userData.specialization.university
                + " // "
                + userData.specialization.faculty
                + " // "
                + userData.specialization.year)

        var semesters = arrayOf("I", "II")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, semesters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSemester.adapter = adapter
        courseSemester.setSelection(0)
        adapter.notifyDataSetChanged()

        btnAdd.setOnClickListener {
            if (courseName.text.isEmpty()) {
                courseName.error = "Course name is required"
                return@setOnClickListener
            }

            if (courseProfessor.text.isEmpty()) {
                courseProfessor.error = "Professor name is required"
                return@setOnClickListener
            }

            if (courseID.text.isEmpty()) {
                courseID.error = "Course ID is required"
                return@setOnClickListener
            }

            val semester = Utils.transformToArabic(courseSemester.selectedItem.toString())
            if (semester == "invalid number") {
                Snackbar.make(mainLayout, "Invalid semester selected", Snackbar.LENGTH_SHORT).show()
                courseSemester.setSelection(0)
                return@setOnClickListener
            }

            val course: MutableMap<String, Any> = HashMap()

            course["id"] = courseID.text.toString().uppercase()
            course["name"] = courseName.text.toString()
            course["professor"] = courseProfessor.text.toString()
            course["semester"] = semester

            yearRef.document(courseID.text.toString().uppercase()).set(course)
                .addOnSuccessListener {
                    val intent = Intent(this, CourseBrowserActivity::class.java)

                    val bundle = Bundle()
                    bundle.putSerializable("user", userData)
                    intent.putExtras(bundle)

                    startActivity(intent)
                }.addOnFailureListener {
                Snackbar.make(mainLayout, "Error: " + it.message, Snackbar.LENGTH_SHORT).show()

                val intent = Intent(this, CourseBrowserActivity::class.java)

                val bundle = Bundle()
                bundle.putSerializable("user", userData)
                intent.putExtras(bundle)

                startActivity(intent)
            }
        }
    }
}