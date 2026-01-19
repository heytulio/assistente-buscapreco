package com.troot.assistentebuscapreco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troot.assistentebuscapreco.model.Message
import com.troot.assistentebuscapreco.model.Sender
import com.troot.assistentebuscapreco.network.RetrofitClient
import com.troot.assistentebuscapreco.network.SearchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        _messages.value = listOf(Message("Olá! O que você gostaria de pesquisar hoje?", Sender.ASSISTANT))
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return

        val userMessage = Message(text = query, sender = Sender.USER)
        _messages.value += userMessage

        val loadingMessage = Message(text = "Buscando as melhores ofertas...", sender = Sender.ASSISTANT)
        _messages.value += loadingMessage

        viewModelScope.launch {
            try {
                val request = SearchRequest(query = query)
                val response = RetrofitClient.apiService.searchOffers(request)

                _messages.value = _messages.value.filterNot { it === loadingMessage }

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val offers = response.body()!!
                    
                    val productMessages = offers.map {
                        Message(
                            text = "",
                            sender = Sender.ASSISTANT,
                            isProduct = true,
                            productTitle = it.produto,
                            price = it.preco,
                            shop = "Vendido por: ${it.loja}",
                            productUrl = it.link
                        )
                    }
                    _messages.value += productMessages
                } else {
                    val errorMessage = Message("Desculpe, não encontrei nenhuma oferta para '$query'. Tente outros termos.", Sender.ASSISTANT)
                    _messages.value += errorMessage
                }

            } catch (e: Exception) {
                _messages.value = _messages.value.filterNot { it === loadingMessage }
                val networkError = Message("Erro de conexão. Verifique sua internet ou se a API está online.", Sender.ASSISTANT)
                _messages.value += networkError
            }
        }
    }
}
