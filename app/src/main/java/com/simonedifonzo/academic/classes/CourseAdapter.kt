package com.simonedifonzo.academic.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.simonedifonzo.academic.R

public class CourseAdapter(options: FirestoreRecyclerOptions<Course>) :
    FirestoreRecyclerAdapter<Course, CourseAdapter.CourseHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.course_item,
            parent, false
        )

        return CourseHolder(v)
    }

    protected override fun onBindViewHolder(holder: CourseHolder, position: Int, model: Course) {
        holder.tvTitle.text         = model.name
        holder.tvDescription.text   = model.professor
        holder.tvCode.text          = model.id
    }

    inner class CourseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.text_view_title)
        var tvDescription: TextView = itemView.findViewById(R.id.text_view_description)
        var tvCode: TextView = itemView.findViewById(R.id.text_view_code)

    }
}