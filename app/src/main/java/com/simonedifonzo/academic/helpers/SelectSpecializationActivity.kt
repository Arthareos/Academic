package com.simonedifonzo.academic.helpers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.simonedifonzo.academic.LauncherActivity
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import java.util.*


class SelectSpecializationActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()
    private var userData: User = User()

    private lateinit var universitiesRef: CollectionReference
    private var universitiesIdList: MutableList<String> = mutableListOf()
    private var universitiesNameList: MutableList<String> = mutableListOf()

    private lateinit var facultiesRef: CollectionReference
    private var facultiesIdList: MutableList<String> = mutableListOf()
    private var facultiesNameList: MutableList<String> = mutableListOf()

    private var yearsIdList: MutableList<String> = mutableListOf()
    private var yearsNameList: MutableList<String> = mutableListOf()

    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var universitySpinner: Spinner
    private lateinit var facultySpinner: Spinner
    private lateinit var yearSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_specialization)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        universitiesRef = service.firestore.collection("universities")

        initInfo()
    }

    private fun initViews() {
        btnAdd = findViewById(R.id.button_create)
        btnBack = findViewById(R.id.back_button)

        universitySpinner = findViewById(R.id.text_university)
        facultySpinner = findViewById(R.id.text_faculty)
        yearSpinner = findViewById(R.id.text_year)
    }

    private fun initInfo() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        facultySpinner.isEnabled = false
        facultySpinner.isClickable = false
        yearSpinner.isEnabled = false
        yearSpinner.isClickable = false

        feedUniversities()

        universitySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                universitySpinner.setSelection(position)

                facultiesIdList.clear()
                facultiesNameList.clear()
                yearsIdList.clear()
                yearsNameList.clear()

                feedFaculties(universitiesIdList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        facultySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                facultySpinner.setSelection(position)
                feedYears(facultiesIdList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        yearSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                yearSpinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnAdd.setOnClickListener {
            service.firestore.collection("users")
                .document(service.auth.uid.toString())
                .update(
                    "specialization",
                    universitiesIdList[universitySpinner.selectedItemPosition]
                            + " " + facultiesIdList[facultySpinner.selectedItemPosition]
                            + " " + yearsIdList[yearSpinner.selectedItemPosition]
                )

            val intent = Intent(this@SelectSpecializationActivity, LauncherActivity::class.java)
            startActivity(intent)
        }
    }

    private fun feedUniversities() {
        universitiesIdList.clear()
        universitiesNameList.clear()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, universitiesNameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        universitySpinner.adapter = adapter

        universitiesRef.get().addOnSuccessListener {
            for (university in it) {
                universitiesIdList.add(university.id)
                universitiesNameList.add(university.getString("name").toString())
            }

            adapter.notifyDataSetChanged()
        }
    }

    private fun feedFaculties(university: String) {
        facultiesIdList.clear()
        facultiesNameList.clear()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, facultiesNameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        facultySpinner.adapter = adapter

        facultiesRef = universitiesRef.document(university).collection("faculties")
        facultiesRef.get().addOnSuccessListener {
            for (faculty in it) {
                facultiesIdList.add(faculty.id)
                facultiesNameList.add(faculty.getString("name").toString())
            }

            adapter.notifyDataSetChanged()
        }

        facultySpinner.isEnabled = true
        facultySpinner.isClickable = true
    }

    private fun feedYears(faculty: String) {
        yearsIdList.clear()
        yearsNameList.clear()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearsNameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapter

        facultiesRef.document(faculty).get().addOnCompleteListener {

            val document = it.result
            if (it.isSuccessful && document != null) {
                val data = document.get("bachelor").toString()

                if (data != "null") {
                    val array = data.subSequence(1, data.length - 1).split(", ")

                    for (year in array) {
                        var yearName = ""
                        when (year[0].toString()) {
                            "l" -> yearName += "Licenta Anul "
                            "m" -> yearName += "Master Anul "
                        }
                        yearName += year[1].toString()

                        yearsIdList.add(year)
                        yearsNameList.add(yearName)
                    }
                }
            }

            adapter.notifyDataSetChanged()
        }

        yearSpinner.isEnabled = true
        yearSpinner.isClickable = true
    }
}