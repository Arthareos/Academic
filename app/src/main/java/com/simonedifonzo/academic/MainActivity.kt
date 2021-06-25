package com.simonedifonzo.academic

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    private var service:    GoogleService   = GoogleService()
    private var userData:   User            = User()

    private lateinit var mainLayout : LinearLayout

    private var isLoaded: Boolean = false
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        mainLayout = findViewById(R.id.main_layout)

        GlobalScope.launch(Dispatchers.IO) {
//            userData = usersReference.get().await().toObject(User::class.java)!!

            val data = async { loadUserData() }
            data.await()
            data.join()
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

//                Snackbar.make(mainLayout, "document: " + array, Snackbar.LENGTH_SHORT).show()

                for (course in array) {
                    Snackbar.make(mainLayout, "inFor: " + course, Snackbar.LENGTH_SHORT).show()

                    userData.starredCourses.add(Course.generateCourse(service, course))
                }

//                Snackbar.make(mainLayout, "document: " + userData.starredCourses[0].name, Snackbar.LENGTH_SHORT).show()
            }
        }.await()

        isLoaded = true
//        Snackbar.make(mainLayout, "data loaded successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            return

        } else {
            Toast.makeText(this@MainActivity, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }

        backPressedTime = System.currentTimeMillis()
    }
}