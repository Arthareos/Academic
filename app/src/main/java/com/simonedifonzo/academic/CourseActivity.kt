package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.simonedifonzo.academic.classes.*
import com.simonedifonzo.academic.helpers.ResourceTypeFragment
import java.util.*

class CourseActivity : AppCompatActivity(), ResourceAdapter.OnClickListener {

    private var service: GoogleService  = GoogleService()
    private var userData: User          = User()
    private var course: Course          = Course()

    private lateinit var resourceRef: CollectionReference
    private lateinit var adapter: ResourceAdapter

    private lateinit var recyclerResource : RecyclerView
    private lateinit var btnAdd : FloatingActionButton

    private lateinit var btnBack: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtProfessor: TextView

    private lateinit var resourceTypeFragment: ResourceTypeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData    = intent.getSerializableExtra("user") as User
        course      = intent.getSerializableExtra("course") as Course

        resourceRef = service.firestore.collection("universities")
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
        txtName             = findViewById(R.id.course_name)
        txtProfessor        = findViewById(R.id.course_professor)

        recyclerResource    = findViewById(R.id.recycler_view)
        btnAdd              = findViewById(R.id.button_add)
    }

    private fun initInfo() {

        btnBack.setOnClickListener {
            onBackPressed()
        }

        txtName.text        = course.name
        txtProfessor.text   = course.professor

        if (userData.rank == "user") {
            btnAdd.visibility = View.GONE
        }

        resourceTypeFragment = ResourceTypeFragment(userData = userData, service = service)
        btnAdd.setOnClickListener {
            resourceTypeFragment.show(supportFragmentManager, "resourceTypeFragment")
        }

        val query: Query = resourceRef.orderBy("name", Query.Direction.ASCENDING)

        val options: FirestoreRecyclerOptions<Resource> = FirestoreRecyclerOptions.Builder<Resource>()
            .setQuery(query, Resource::class.java)
            .build()

        adapter = ResourceAdapter(options, this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position", Toast.LENGTH_SHORT).show()

        val clickedItem = adapter.getItem(position)
    }

    override fun onBackPressed() {
        val intent = Intent(this@CourseActivity, CourseBrowserActivity::class.java)

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