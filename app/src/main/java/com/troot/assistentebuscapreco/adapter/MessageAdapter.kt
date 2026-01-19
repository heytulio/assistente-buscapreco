package com.troot.assistentebuscapreco.adapter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.troot.assistentebuscapreco.R
import com.troot.assistentebuscapreco.databinding.ItemMessageBinding
import com.troot.assistentebuscapreco.model.Message
import com.troot.assistentebuscapreco.model.Sender

class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        val binding = holder.binding

        binding.tvMessageContent.text = message.text

        val params = binding.tvMessageContent.layoutParams as ConstraintLayout.LayoutParams
        if (message.sender == Sender.USER) {
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvMessageContent.background = ContextCompat.getDrawable(binding.root.context, R.drawable.bg_message_user)
            binding.tvMessageContent.setTextColor(Color.WHITE)
        } else { // ASSISTANT
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvMessageContent.background = ContextCompat.getDrawable(binding.root.context, R.drawable.bg_message_assistant)
            binding.tvMessageContent.setTextColor(Color.BLACK)
        }
        binding.tvMessageContent.layoutParams = params

        if (message.isProduct) {
            binding.cardProduct.visibility = View.VISIBLE
            binding.tvProductTitle.text = message.productTitle
            binding.tvPrice.text = message.price
            binding.tvShop.text = message.shop

            binding.imgProduct.visibility = View.GONE

            // Lógica do clique do botão
            binding.btnViewOffer.setOnClickListener {
                message.productUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                    binding.root.context.startActivity(intent)
                }
            }

            if (message.text.isBlank()) {
                binding.tvMessageContent.visibility = View.GONE
            } else {
                binding.tvMessageContent.visibility = View.VISIBLE
            }
        } else {
            binding.cardProduct.visibility = View.GONE
            binding.tvMessageContent.visibility = View.VISIBLE
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
