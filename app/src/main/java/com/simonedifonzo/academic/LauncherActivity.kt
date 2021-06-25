package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.simonedifonzo.academic.classes.Course
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.Specialization
import com.simonedifonzo.academic.classes.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class LauncherActivity : AppCompatActivity() {
    private var service:    GoogleService   = GoogleService()
    private var userData:   User            = User()

    private lateinit var mainLayout : ConstraintLayout

    private lateinit var txt_title: TextView
    private lateinit var layout_navigation: LinearLayout
    private lateinit var btn_login: Button
    private lateinit var btn_register: Button

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        service = GoogleService()

        mainLayout          = findViewById(R.id.main_layout)
        txt_title           = findViewById(R.id.app_name)
        layout_navigation   = findViewById(R.id.bottom_navigation)
        btn_login           = findViewById(R.id.btn_login)
        btn_register        = findViewById(R.id.btn_register)

        GlobalScope.launch(Dispatchers.IO) {

            Snackbar.make(mainLayout, "loading user data...", Snackbar.LENGTH_SHORT).show()

            var googleData = async { GoogleService() }
            service = googleData.await()
            googleData.join()

            var data = async { loadUserData() }
            data.await()
            data.join()

            Snackbar.make(mainLayout, "user data loaded successfully", Snackbar.LENGTH_SHORT).show()

            if (service.auth.currentUser != null) {
                val intent = Intent(this@LauncherActivity, MainActivity::class.java)

                val bundle = Bundle()
                bundle.putSerializable("user", userData)
                intent.putExtras(bundle)

                startActivity(intent)
                this.cancel()
            }

            withContext(Dispatchers.Main) {
                txt_title.visibility            = View.VISIBLE
                layout_navigation.visibility    = View.VISIBLE
            }

            btn_login.setOnClickListener {
                val intent = Intent(this@LauncherActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            btn_register.setOnClickListener {
                val intent = Intent(this@LauncherActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun loadUserData() {
        Snackbar.make(mainLayout, "loading user data...", Snackbar.LENGTH_SHORT).show()

        var usersReference = service.firestore
            .collection("users")
            .document(service.auth.uid.toString())

        usersReference.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->

            var document = task.result
            if (task.isSuccessful && document != null) {
                userData.email      = document.getString("email").toString()
                userData.first      = document.getString("first").toString()
                userData.last       = document.getString("last").toString()
                userData.profilePic = document.getString("profilePic").toString()
                userData.lastChange = document.getString("lastChange").toString()

                userData.specialization = Specialization.createSpecialization(
                    document.getString("specialization").toString()
                )

                userData.starredCourses.clear()

                val data     = document.get("starredCourses").toString()
                val array    = data.subSequence(1, data.length - 1).split(", ")
                for (course in array) {
                    Snackbar.make(mainLayout, "inFor: " + course, Snackbar.LENGTH_SHORT).show()

                    userData.starredCourses.add(Course.generateCourse(service, course))
                }
            }
        }.await()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            return

        } else {
            Toast.makeText(this@LauncherActivity, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }

        backPressedTime = System.currentTimeMillis()
    }
}