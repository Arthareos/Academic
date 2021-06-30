package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.simonedifonzo.academic.classes.Course
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.Specialization
import com.simonedifonzo.academic.classes.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var service:    GoogleService   = GoogleService()
    private var userData:   User            = User()

    private lateinit var emailBox:    EditText
    private lateinit var passwordBox: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin:    Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        emailBox      = findViewById(R.id.emailBox)
        passwordBox   = findViewById(R.id.passwordBox)
        btnLogin      = findViewById(R.id.btn_login)
        btnRegister   = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener(View.OnClickListener {
            val email = emailBox.text.toString().trim { it <= ' ' }
            val password = passwordBox.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {
                emailBox.error = "Email is required!"
                return@OnClickListener
            }

            if (password.isEmpty()) {
                passwordBox.error = "Password is required!"
                return@OnClickListener
            }

            if (password.length <= 6) {
                passwordBox.error = "Password must be longer than 6 characters!"
                return@OnClickListener
            }

            service.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        GlobalScope.launch(Dispatchers.IO) {
                            var data = async { loadUserData() }
                            data.await()
                            data.join()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)

                            val bundle = Bundle()
                            bundle.putSerializable("user", userData)
                            intent.putExtras(bundle)

                            startActivity(intent)
                        }

                    } else {
                        if (task.exception?.message == "auth/invalid-email") {
                            emailBox.error = "No account is linked to this email"
                        }

                        if (task.exception?.message == "auth/user-disabled") {
                            emailBox.error = "This account has been disabled, contact support"
                        }

                        if (task.exception?.message == "auth/wrong-password") {
                            passwordBox.error = "Email is wrong"
                        }

                        val message: String? = task.exception?.message
                        Toast.makeText(this@LoginActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }

    private suspend fun loadUserData() {
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

                if(document.getString("specialization").toString() != "null") {
                    userData.specialization = Specialization.createSpecialization(
                        document.getString("specialization").toString())
                }

//                userData.starredCourses.clear()
//
//                val data     = document.get("starredCourses").toString()
//                val array    = data.subSequence(1, data.length - 1).split(", ")
//                for (course in array) {
//                    userData.starredCourses.add(Course.generateCourse(service, course))
//                }
            }
        }.await()
    }
}