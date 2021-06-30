package com.simonedifonzo.academic.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.simonedifonzo.academic.R

public class AcademicWorkAdapter(
    private val options: FirestoreRecyclerOptions<AcademicWork>,
    private val listener: OnClickListener
) :
    FirestoreRecyclerAdapter<AcademicWork, AcademicWorkAdapter.WorkHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.academicwork_item,
            parent, false
        )

        return WorkHolder(v)
    }

    protected override fun onBindViewHolder(
        holder: WorkHolder,
        position: Int,
        model: AcademicWork
    ) {
        holder.tvTitle.text = model.name
        holder.tvDescription.text = model.author
        holder.tvCode.text = model.type
    }

    inner class WorkHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvTitle: TextView = itemView.findViewById(R.id.text_view_title)
        var tvDescription: TextView = itemView.findViewById(R.id.text_view_description)
        var tvCode: TextView = itemView.findViewById(R.id.text_view_type)

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