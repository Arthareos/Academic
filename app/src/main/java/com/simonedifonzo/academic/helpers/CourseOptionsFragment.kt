package com.simonedifonzo.academic.helpers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.classes.Course
import com.simonedifonzo.academic.classes.User

class CourseOptionsFragment(
    private var userData: User,
    private var course: Course
) :
    BottomSheetDialogFragment() {

    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.courseoptions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnEdit = view.findViewById(R.id.btn_edit)
        btnEdit.setOnClickListener {
            val intent = Intent(context, EditCourseActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            bundle.putSerializable("course", course)
            intent.putExtras(bundle)

            startActivity(intent)
        }
    }
}