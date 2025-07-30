package com.example.projectfluid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.mahmoud_dev01.fluid.ClippingTargetViewHolder

class ClippingDemoAdapter(private val context: Context, private val messages: ArrayList<Message>) : RecyclerView.Adapter<ClippingDemoAdapter.ClippingViewHolder>() {

    companion object {
        const val OUTGOING_TYPE = 0
        const val INCOMING_TYPE = 1 // Example, not used in this simple adapter
    }

    class ClippingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ClippingTargetViewHolder {
        // Expose the TextView as the clipping target for the decoration
        override val clippingTarget: View = itemView.findViewById(R.id.text)
        private val textView: TextView = itemView.findViewById(R.id.text)

        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClippingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_clipping_message, parent, false)
        return ClippingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClippingViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
}