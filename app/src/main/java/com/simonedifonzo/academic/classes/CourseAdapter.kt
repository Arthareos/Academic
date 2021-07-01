package com.simonedifonzo.academic.classes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.simonedifonzo.academic.R
import com.simonedifonzo.academic.helpers.CourseOptionsFragment
import com.simonedifonzo.academic.helpers.ResourceTypeFragment

public class CourseAdapter(
    private val options: FirestoreRecyclerOptions<Course>,
    private val listener: OnClickListener,
    private val userData: User,
    private val supportFragmentManager: FragmentManager
) :
    FirestoreRecyclerAdapter<Course, CourseAdapter.CourseHolder>(options) {

    private var courseOptionsFragmentArray: ArrayList<CourseOptionsFragment> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.course_item,
            parent, false
        )

        return CourseHolder(v)
    }

    @SuppressLint("SetTextI18n")
    protected override fun onBindViewHolder(holder: CourseHolder, position: Int, model: Course) {
        holder.tvTitle.text = model.name
        holder.tvDescription.text = model.id + " • Prof. " + model.professor

        courseOptionsFragmentArray.add(CourseOptionsFragment(userData = userData, course = this.getItem(position)))
        holder.btnOptions.setOnClickListener {
            courseOptionsFragmentArray[position].show(supportFragmentManager, "resourceTypeFragment")
        }

        if (userData.rank != "admin") {
            holder.btnOptions.visibility = View.INVISIBLE
        }
    }

    inner class CourseHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvTitle: TextView = itemView.findViewById(R.id.text_view_title)
        var tvDescription: TextView = itemView.findViewById(R.id.text_view_description)
        var btnOptions: ImageView = itemView.findViewById(R.id.button_options)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}