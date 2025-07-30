package com.example.projectfluid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MaskingDemoAdapter(private val context: Context, private val messages: ArrayList<Message>) : RecyclerView.Adapter<MaskingDemoAdapter.MaskingViewHolder>() {

    class MaskingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text)
        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaskingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_masking_message, parent, false)
        return MaskingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaskingViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
}