package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.simonedifonzo.academic.helpers.ResourceTypeFragment
import java.util.*

class AcademicWorkBrowserActivity : AppCompatActivity(), CourseAdapter.OnClickListener {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var coursesRef: CollectionReference
    private lateinit var adapter: CourseAdapter

    private lateinit var mainLayout : LinearLayout
    private lateinit var btnBack : ImageView

    private lateinit var txtPath : TextView
    private lateinit var recyclerWorks : RecyclerView
    private lateinit var btnAdd : FloatingActionButton

    private lateinit var resourceTypeFragment: ResourceTypeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_academic_work_browser)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        coursesRef = service.firestore.collection("universities")
            .document(userData.specialization.university)
            .collection("faculties")
            .document(userData.specialization.faculty)
            .collection("academicWorks")

        initInfo()
    }

    private fun initViews() {
        mainLayout      = findViewById(R.id.main_layout)
        btnBack         = findViewById(R.id.back_button)
        txtPath         = findViewById(R.id.txt_path)

        recyclerWorks   = findViewById(R.id.recycler_view)
        btnAdd          = findViewById(R.id.button_add)
    }

    private fun initInfo() {

        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtPath.text = (userData.specialization.university
                + " // "
                + userData.specialization.faculty
                + " // "
                + "academicWorks")


        if (userData.rank == "user") {
            btnAdd.visibility = View.GONE
        }

        resourceTypeFragment = ResourceTypeFragment(userData = userData, service = service)
        btnAdd.setOnClickListener {
            // TODO: Add functionality

            Toast.makeText(this, "Pending functionality", Toast.LENGTH_SHORT).show()

//            resourceTypeFragment.show(supportFragmentManager, "resourceTypeFragment")
        }

        val query: Query = coursesRef.orderBy("name", Query.Direction.ASCENDING)

        val options: FirestoreRecyclerOptions<Course> = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(query, Course::class.java)
            .build()

        adapter = CourseAdapter(options, this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        // TODO: Add functionality

//        Toast.makeText(this, "Item $position", Toast.LENGTH_SHORT).show()
//        val clickedItem = adapter.getItem(position).

//        val intent = Intent(this@AcademicWorkBrowserActivity, CourseActivity::class.java)
//
//        val bundle = Bundle()
//        bundle.putSerializable("user", userData)
//        bundle.putSerializable("course", adapter.getItem(position))
//        intent.putExtras(bundle)
//
//        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this@AcademicWorkBrowserActivity, MainActivity::class.java)

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