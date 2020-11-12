package com.atiurin.sampleapp.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.entities.Message
import com.atiurin.sampleapp.data.repositories.CURRENT_USER


class MessageAdapter(private var messages: ArrayList<Message>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Message)
    }
    class MessageViewHolder(val view: LinearLayout) : RecyclerView.ViewHolder(view)

    open fun updateData(data: ArrayList<Message>) {
        messages = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false) as LinearLayout
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.view.setOnClickListener {
            listener.onItemClick(messages.get(position))
        }
        val messageText = holder.view.findViewById(R.id.message_text) as TextView
        val authorName = holder.view.findViewById(R.id.author) as TextView
        val message = messages[position]
        messageText.text = message.text
        if (message.authorId == CURRENT_USER.id){
            val view = holder.view.get(0)
            val cardView = view.findViewById<CardView>(R.id.card_view)
            cardView.setCardBackgroundColor(view.context.resources.getColor(R.color.colorLight))
            val layoutParams = view.layoutParams
            if (layoutParams is LinearLayout.LayoutParams){
                layoutParams.gravity = Gravity.RIGHT
            }
            view.layoutParams = layoutParams
        }

    }

    override fun getItemCount() = messages.size
}