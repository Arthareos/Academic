package com.simonedifonzo.academic

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User
import com.simonedifonzo.academic.helpers.SelectSpecializationActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var service:    GoogleService   = GoogleService()
    private var userData:   User            = User()

    private lateinit var mainLayout : LinearLayout

    private lateinit var picProfile : CircleImageView
    private lateinit var picVerified : ImageView
    private lateinit var txtName : TextView
    private lateinit var txtEmail : TextView

    private lateinit var cardAbout : CardView
    private lateinit var cardCourse : CardView
    private lateinit var cardAcademic : CardView
    private lateinit var cardBook : CardView

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Objects.requireNonNull(this.supportActionBar)!!.hide()

        initViews()

        userData = intent.getSerializableExtra("user") as User

        initInfo()

//        GlobalScope.launch(Dispatchers.IO) { {
//            delay(5000)
//            withContext(Dispatchers.Main) {
//                // if we use `Dispatchers.Main` as a coroutine context next two lines will be executed on UI thread.
//                doSomething()
//                doAnotherThing()
//            }
//        }
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.main_layout)

        picProfile  = findViewById(R.id.pic_profile)
        picVerified = findViewById(R.id.verified_tick)
        txtName     = findViewById(R.id.txt_name)
        txtEmail    = findViewById(R.id.txt_email)

        cardAbout       = findViewById(R.id.card_about)
        cardCourse      = findViewById(R.id.card_course)
        cardAcademic    = findViewById(R.id.card_academic)
        cardBook        = findViewById(R.id.card_book)
    }

    private fun initInfo() {

        // Load profile picture if user has one
        if (userData.profilePic != "null") {
            var fileRef = service.storage?.child(userData.profilePic)

            fileRef?.downloadUrl?.addOnSuccessListener {
                Picasso.get().load(it).into(picProfile)
            }
        }

        // Check if user is email verified
        if (service.auth.currentUser?.isEmailVerified != true)
        {
            picVerified.isVisible = false
        }

        // Load user data into textViews
        txtName.setText(userData.first + " " + userData.last)
        txtEmail.setText(userData.email)

        // Routes init
        cardAbout.setOnClickListener {
            val intent = Intent(this@MainActivity, PersonalActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        cardCourse.setOnClickListener {
            if (userData.specialization.university == "null") {
                Snackbar.make(mainLayout, "You don't have a specialization selected", Snackbar.LENGTH_LONG)
                    .setAction("Select") {
                        val intent = Intent(this@MainActivity, SelectSpecializationActivity::class.java)

                        val bundle = Bundle()
                        bundle.putSerializable("user", userData)
                        intent.putExtras(bundle)

                        startActivity(intent)
                    }.show()

                return@setOnClickListener
            }

            val intent = Intent(this@MainActivity, CourseBrowserActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        cardAcademic.setOnClickListener {
            if (userData.specialization.university == "null") {
                Snackbar.make(mainLayout, "You don't have a specialization selected", Snackbar.LENGTH_LONG)
                    .setAction("Select") {
                        val intent = Intent(this@MainActivity, SelectSpecializationActivity::class.java)

                        val bundle = Bundle()
                        bundle.putSerializable("user", userData)
                        intent.putExtras(bundle)

                        startActivity(intent)
                    }.show()

                return@setOnClickListener
            }

            val intent = Intent(this@MainActivity, AcademicWorkBrowserActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            intent.putExtras(bundle)

            startActivity(intent)
        }
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