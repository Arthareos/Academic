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
import com.simonedifonzo.academic.classes.GoogleService
import com.simonedifonzo.academic.classes.User

class ResourceTypeFragment(private var service: GoogleService, private var userData: User, private var course: Course) :
    BottomSheetDialogFragment() {

    private lateinit var btnPdf : Button
    private lateinit var btnLink : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.resourcetype_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPdf = view.findViewById(R.id.btn_pdf)
        btnPdf.setOnClickListener {
            val intent = Intent(context, CreateDocumentResource::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            bundle.putSerializable("course", course)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        btnLink = view.findViewById(R.id.btn_link)
        btnLink.setOnClickListener {
            val intent = Intent(context, CreateMediaResource::class.java)

            val bundle = Bundle()
            bundle.putSerializable("user", userData)
            bundle.putSerializable("course", course)
            intent.putExtras(bundle)

            startActivity(intent)
        }
    }
}