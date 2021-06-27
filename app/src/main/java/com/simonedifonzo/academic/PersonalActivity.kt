package com.simonedifonzo.academic

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PersonalActivity : AppCompatActivity() {
    private var service:    GoogleService   = GoogleService()
    private var userData:   User            = User()

    private lateinit var mainLayout : LinearLayout
    private lateinit var btnBack : ImageView

    private lateinit var picProfile : CircleImageView
    private lateinit var picVerified : ImageView
    private lateinit var txtName : TextView
    private lateinit var txtEmail : TextView

    private lateinit var btnChangePic: Button
    private lateinit var btnVerify: Button
    private lateinit var btnLogoff: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        initInfo()
    }

    private fun initViews() {
        mainLayout  = findViewById(R.id.main_layout)
        btnBack     = findViewById(R.id.back_button)

        picProfile  = findViewById(R.id.pic_profile)
        picVerified = findViewById(R.id.verified_tick)
        txtName     = findViewById(R.id.txt_name)
        txtEmail    = findViewById(R.id.txt_email)

        btnChangePic    = findViewById(R.id.btn_picture)
        btnVerify       = findViewById(R.id.btn_verify)
        btnLogoff       = findViewById(R.id.btn_logoff)
    }

    private fun initInfo() {

        // Load user data into textViews
        txtName.setText(userData.first + " " + userData.last)
        txtEmail.setText(userData.email)

        // Load profile picture if user has one
        if (userData.profilePic != "null") {
            var fileRef = service.storage?.child(userData.profilePic)

            fileRef?.downloadUrl?.addOnSuccessListener {
                Picasso.get().load(it).into(picProfile)
            }
        }

        // Check if user is email verified
        if (service.auth.currentUser?.isEmailVerified == true) {
            picVerified.visibility  = View.VISIBLE
            btnVerify.visibility    = View.GONE
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Routes init
        btnChangePic.setOnClickListener {
            // TODO: Add code
        }

        btnVerify.setOnClickListener {
            service.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener() {
                if (it.isSuccessful) {
                    Snackbar.make(mainLayout, "Verification email sent! Open email?", Snackbar.LENGTH_SHORT)
                        .setAction("Open email") {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                            this@PersonalActivity.startActivity(intent)
                        }
                        .show()
                } else {
                    Toast.makeText(
                        this@PersonalActivity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnLogoff.setOnClickListener {
            service.auth.signOut()

            Toast.makeText(this@PersonalActivity, "You've been logged off!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@PersonalActivity, LauncherActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@PersonalActivity, MainActivity::class.java)

        val bundle = Bundle()
        bundle.putSerializable("user", userData)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}