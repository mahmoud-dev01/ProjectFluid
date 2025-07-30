package com.example.projectfluid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.mahmoud_dev01.fluid.ClippingTargetViewHolder

class ChatAdapter(
    private val context: Context,
    private val messages: ArrayList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val OUTGOING_MESSAGE_TYPE = 0
        const val INCOMING_MESSAGE_TYPE = 1
    }

    // ViewHolder for outgoing messages (right-aligned)
    class OutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ClippingTargetViewHolder {
        // Expose the TextView as the clipping target for the decoration
        override val clippingTarget: View = itemView.findViewById(R.id.text)
        private val textView: TextView = itemView.findViewById(R.id.text)

        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    // ViewHolder for incoming messages (left-aligned)
    class IncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ClippingTargetViewHolder {
        override val clippingTarget: View = itemView.findViewById(R.id.text)
        private val textView: TextView = itemView.findViewById(R.id.text)

        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isMine) OUTGOING_MESSAGE_TYPE else INCOMING_MESSAGE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == OUTGOING_MESSAGE_TYPE) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_outgoing_message, parent, false)
            OutgoingViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_incoming_message, parent, false)
            IncomingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is OutgoingViewHolder) {
            holder.bind(message)
        } else if (holder is IncomingViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size
}