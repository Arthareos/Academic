package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.simonedifonzo.academic.classes.Course
import com.simonedifonzo.academic.classes.CourseAdapter
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import java.util.*

class CourseBrowserActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var coursesRef: CollectionReference
    private lateinit var adapter: CourseAdapter

    private lateinit var mainLayout : LinearLayout
    private lateinit var btnBack : ImageView

    private lateinit var txtPath : TextView
    private lateinit var recyclerCourses : RecyclerView
    private lateinit var btnAdd : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_browser)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        coursesRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection(userData.specialization.year)

        initInfo()
    }

    private fun initViews() {
        mainLayout      = findViewById(R.id.main_layout)
        btnBack         = findViewById(R.id.back_button)
        txtPath         = findViewById(R.id.txt_path)

        recyclerCourses = findViewById(R.id.recycler_view)
        btnAdd          = findViewById(R.id.button_add)
    }

    private fun initInfo() {

        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtPath.text = (userData.specialization.university
                + " / "
                + userData.specialization.faculty
                + " / "
                + userData.specialization.year)

        val query: Query = coursesRef.orderBy("name", Query.Direction.ASCENDING)

        val options: FirestoreRecyclerOptions<Course> = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(query, Course::class.java)
            .build()

        adapter = CourseAdapter(options)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        val intent = Intent(this@CourseBrowserActivity, MainActivity::class.java)

        val bundle = Bundle()
        bundle.putSerializable("user", userData)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}