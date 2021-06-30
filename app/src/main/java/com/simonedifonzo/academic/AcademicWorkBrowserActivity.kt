package com.simonedifonzo.academic

import android.content.Intent
import android.net.Uri
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
import com.simonedifonzo.academic.classes.*
import com.simonedifonzo.academic.helpers.CreateAcademicWorkActivity
import com.simonedifonzo.academic.helpers.ResourceTypeFragment
import java.util.*

class AcademicWorkBrowserActivity : AppCompatActivity(), AcademicWorkAdapter.OnClickListener {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var coursesRef: CollectionReference
    private lateinit var adapter: AcademicWorkAdapter

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
            .collection("academic")

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
                + "academic")


        if (userData.rank == "user") {
            btnAdd.visibility = View.GONE
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, CreateAcademicWorkActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        val query: Query = coursesRef.orderBy("name", Query.Direction.ASCENDING)

        val options: FirestoreRecyclerOptions<AcademicWork> = FirestoreRecyclerOptions.Builder<AcademicWork>()
            .setQuery(query, AcademicWork::class.java)
            .build()

        adapter = AcademicWorkAdapter(options, this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val clickedItem = adapter.getItem(position)

        var fileRef = service.storage?.child(clickedItem.link)

        fileRef?.downloadUrl?.addOnSuccessListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.toString()))
            startActivity(browserIntent)

        }?.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
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