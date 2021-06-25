package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import java.util.*

class LauncherActivity : AppCompatActivity() {
    private lateinit var service: GoogleService
    private lateinit var userData: User

    private lateinit var txt_title: TextView
    private lateinit var layout_navigation: LinearLayout
    private lateinit var btn_login: Button
    private lateinit var btn_register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        txt_title           = findViewById(R.id.app_name)
        layout_navigation   = findViewById(R.id.bottom_navigation)
        btn_login           = findViewById(R.id.btn_login)
        btn_register        = findViewById(R.id.btn_register)

        service = GoogleService()

        if (service.auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        txt_title.visibility            = View.VISIBLE
        layout_navigation.visibility    = View.VISIBLE

        btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}