package com.simonedifonzo.academic.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.simonedifonzo.academic.R

public class ResourceAdapter(
    private val options: FirestoreRecyclerOptions<Resource>,
    private val listener: OnClickListener
) :
    FirestoreRecyclerAdapter<Resource, ResourceAdapter.ResourceHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.resource_item,
            parent, false
        )

        return ResourceHolder(v)
    }

    protected override fun onBindViewHolder(holder: ResourceHolder, position: Int, model: Resource) {
        holder.tvTitle.text         = model.name
        holder.tvDescription.text   = model.description

        //web, youtube, pdf, word
        when (model.type) {
            "web"       -> holder.imgType.setImageResource(R.drawable.ic_internet)
            "youtube"   -> holder.imgType.setImageResource(R.drawable.ic_youtube)
            "pdf"       -> holder.imgType.setImageResource(R.drawable.ic_pdf)
            "word"      -> holder.imgType.setImageResource(R.drawable.ic_word)
        }
    }

    inner class ResourceHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvTitle         : TextView  = itemView.findViewById(R.id.text_view_title)
        var tvDescription   : TextView  = itemView.findViewById(R.id.text_view_description)
        var imgType         : ImageView = itemView.findViewById(R.id.icon_type)

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