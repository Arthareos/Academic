package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simonedifonzo.academic.classes.GoogleService
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var service: GoogleService = GoogleService()

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
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
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
}