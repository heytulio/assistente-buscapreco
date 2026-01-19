package com.troot.assistentebuscapreco.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.troot.assistentebuscapreco.adapter.MessageAdapter
import com.troot.assistentebuscapreco.databinding.ActivityMainBinding
import com.troot.assistentebuscapreco.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.rvMessages.apply {
            adapter = messageAdapter
            val layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager.stackFromEnd = true
            this.layoutManager = layoutManager
        }
    }

    private fun setupObservers() {
        // Observa o fluxo de mensagens do ViewModel
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                messageAdapter.submitList(messages)
                // Rola para a última mensagem quando a lista é atualizada
                binding.rvMessages.post { binding.rvMessages.scrollToPosition(messages.size - 1) }
            }
        }
    }

    private fun setupClickListeners() {
        // Configura o clique do botão de enviar
        binding.btnSend.setOnClickListener {
            val query = binding.etMessage.text.toString()
            if (query.isNotEmpty()) {
                viewModel.sendMessage(query)
                binding.etMessage.text.clear()
            }
        }
    }
}
