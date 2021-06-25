package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.Utils
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()

    private lateinit var emailBox:      EditText
    private lateinit var passwordBox:   EditText

    private lateinit var firstnameBox:  EditText
    private lateinit var lastnameBox:   EditText

    private lateinit var btnRegister:   Button
    private lateinit var btnBack:       Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        emailBox        = findViewById(R.id.emailBox)
        passwordBox     = findViewById(R.id.passwordBox)

        firstnameBox    = findViewById(R.id.nameBox)
        lastnameBox     = findViewById(R.id.lastnameBox)

        btnRegister     = findViewById(R.id.btn_register)
        btnBack         = findViewById(R.id.btn_back)

        btnRegister.setOnClickListener {
            val email:      String = emailBox.text.toString().trim { it <= ' ' }
            val password:   String = passwordBox.text.toString().trim { it <= ' ' }
            val firstname:  String = firstnameBox.text.toString().trim { it <= ' ' }
            val lastname:   String = lastnameBox.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {
                emailBox.error = "Email is required!"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordBox.error = "Password is required!"
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordBox.error = "Password must be longer than 6 characters!"
                return@setOnClickListener
            }

            service.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        val user: MutableMap<String, Any> = HashMap()

                        user["first"] = firstname
                        user["last"] = lastname
                        user["email"] = email
                        user["rank"] = "user"
                        user["profilePic"] = "null"
                        user["lastChange"] = Utils.currentTimeStamp

                        val courses: List<String> = Vector()

                        user["starredCourses"] = courses
                        user["specialization"] = "null"

                        val documentReference: DocumentReference =
                            service.firestore
                                .collection("users")
                                .document(service.auth.uid.toString())

                        documentReference.set(user).addOnSuccessListener {
                            Toast.makeText(this@RegisterActivity, "Welcome to Booklet!",
                                Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)

                        }.addOnFailureListener { e ->
                            Toast.makeText(this@RegisterActivity, "Error: " + e.message + "!",
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: " + task.exception?.message.toString() + "!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("email", emailBox.text.toString())
            intent.putExtras(bundle)

            startActivity(intent)
        }
    }
}