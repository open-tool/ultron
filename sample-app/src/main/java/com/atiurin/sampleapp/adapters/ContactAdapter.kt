package com.atiurin.sampleapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.view.CircleImageView

class ContactAdapter(private var mDataset: ArrayList<Contact>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Contact)
    }
    class MyViewHolder(val view: LinearLayout) : RecyclerView.ViewHolder(view)

    open fun updateData(data: ArrayList<Contact>) {
        mDataset = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false) as LinearLayout
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.setOnClickListener { listener.onItemClick(mDataset.get(position)) }
        val tvTitle = holder.view.findViewById(R.id.tv_name) as TextView
        val avatar = holder.view.findViewById(R.id.avatar) as CircleImageView
        val status = holder.view.findViewById(R.id.tv_status) as TextView
        tvTitle.text = mDataset[position].name
        status.text = mDataset[position].status
        avatar.setImageDrawable(holder.view.context.getResources().getDrawable(mDataset[position].avatar))
    }

    override fun getItemCount() = mDataset.size
}