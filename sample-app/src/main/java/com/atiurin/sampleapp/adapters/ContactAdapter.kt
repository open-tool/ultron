package com.atiurin.sampleapp.adapters

import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.view.CircleImageView

class ContactAdapter(
    val context: Context,
    private var mDataset: ArrayList<Contact>,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>(), GestureDetector.OnGestureListener {

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
        avatar.setImageDrawable(
            holder.view.context.getResources().getDrawable(mDataset[position].avatar)
        )
        GestureDetectorCompat(context, this)
    }

    override fun getItemCount() = mDataset.size

    //    GestureDetector.OnGestureListener
    override fun onDown(p0: MotionEvent?): Boolean = true
    override fun onShowPress(p0: MotionEvent?) = Unit
    override fun onSingleTapUp(p0: MotionEvent?): Boolean = true
    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = true
    override fun onLongPress(p0: MotionEvent?) = Unit
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = true
}