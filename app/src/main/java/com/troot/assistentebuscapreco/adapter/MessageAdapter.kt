package com.troot.assistentebuscapreco.adapter

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
        val context = binding.root.context

        // Detecta se é mensagem de loading
        val isTyping = message.text == "Digitando..."

        if (isTyping) {
            // Mostra apenas o indicador de digitação
            binding.tvMessageContent.visibility = View.GONE
            binding.typingIndicator.visibility = View.VISIBLE
            binding.cardProduct.visibility = View.GONE

            // Inicia animação dos pontinhos
            startTypingAnimation(binding)
        } else {
            // Mensagem normal
            binding.typingIndicator.visibility = View.GONE
            binding.tvMessageContent.visibility = View.VISIBLE
            binding.tvMessageContent.text = formatText(message.text)

            // Largura máxima do balão
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            binding.tvMessageContent.maxWidth = (screenWidth * 0.80).toInt()

            // Configuração de alinhamento e cores
            val params = binding.tvMessageContent.layoutParams as ConstraintLayout.LayoutParams

            if (message.sender == Sender.USER) {
                params.horizontalBias = 1f
                binding.tvMessageContent.background = ContextCompat.getDrawable(context, R.drawable.bg_message_user)
                binding.tvMessageContent.setTextColor(Color.WHITE)
            } else {
                params.horizontalBias = 0f
                binding.tvMessageContent.background = ContextCompat.getDrawable(context, R.drawable.bg_message_assistant)
                binding.tvMessageContent.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            }

            binding.tvMessageContent.layoutParams = params

            // Lógica dos cards de produto
            if (message.isProduct) {
                binding.cardProduct.visibility = View.VISIBLE
                binding.tvProductTitle.text = message.productTitle
                binding.tvPrice.text = message.price
                binding.tvShop.text = message.shop
                binding.imgProduct.visibility = View.GONE

                binding.btnViewOffer.setOnClickListener {
                    message.productUrl?.let { url ->
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                if (message.text.isBlank()) {
                    binding.tvMessageContent.visibility = View.GONE
                } else {
                    binding.tvMessageContent.visibility = View.VISIBLE
                }
            } else {
                binding.cardProduct.visibility = View.GONE
            }
        }
    }

    private fun startTypingAnimation(binding: ItemMessageBinding) {
        val dot1 = binding.typingIndicator.findViewById<View>(R.id.dot1)
        val dot2 = binding.typingIndicator.findViewById<View>(R.id.dot2)
        val dot3 = binding.typingIndicator.findViewById<View>(R.id.dot3)

        // Animação sequencial dos pontinhos
        animateDot(dot1, 0)
        animateDot(dot2, 200)
        animateDot(dot3, 400)
    }

    private fun animateDot(dot: View, delay: Long) {
        val fadeIn = ObjectAnimator.ofFloat(dot, "alpha", 0.4f, 1f).apply {
            duration = 400
            startDelay = delay
            interpolator = AccelerateDecelerateInterpolator()
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        fadeIn.start()
    }

    private fun formatText(rawText: String): android.text.Spanned {
        if (rawText.isBlank()) return android.text.SpannedString("")

        var html = rawText
        html = html.replace("\n", "<br>")
        html = html.replace("* ", " • ")
        html = html.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<b>$1</b>")

        return android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_COMPACT)
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
    }
}
