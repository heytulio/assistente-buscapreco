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
        // Mensagem de boas-vindas ao iniciar o chat
        showWelcomeMessage()
    }

    private fun showWelcomeMessage() {
        viewModelScope.launch {
            // Mostra "Digitando..."
            val typingMsg = Message(text = "Digitando...", sender = Sender.ASSISTANT)
            _messages.value = listOf(typingMsg)

            // Aguarda 1.5 segundos
            kotlinx.coroutines.delay(1500)

            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            val greeting = when (hour) {
                in 6..11 -> "Bom dia"
                in 12..17 -> "Boa tarde"
                else -> "Boa noite"
            }

            val welcomeMessage = Message(
                text = """$greeting! 👋
Sou o **Assistente Busca Preço**.

Posso te ajudar a encontrar os melhores preços e te ajudar a escolher o melhor produto com uma curadoria especializada.

**Como funciona:**
* Digite o produto que você procura
* Recebo ofertas em tempo real
* Compare preços e economize!

**Exemplos:**
* "Ryzen 5 5500"
* "Notebook Asus TUF Gamer"
* "iPhone 15"

O que você está procurando? 🛍️""",
                sender = Sender.ASSISTANT
            )
            _messages.value = listOf(welcomeMessage)
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return

        val userMessage = Message(text = query, sender = Sender.USER)
        _messages.value += userMessage

        // Adiciona indicador de digitação
        val loadingMessage = Message(text = "Digitando...", sender = Sender.ASSISTANT)
        _messages.value += loadingMessage

        viewModelScope.launch {
            try {
                // Remove a mensagem de loading ANTES de processar
                _messages.value = _messages.value.filterNot { it === loadingMessage }

                val response = RetrofitClient.apiService.searchOffers(SearchRequest(query))

                if (response.isSuccessful && response.body() != null) {
                    val apiData = response.body()!!

                    val textMsg = Message(
                        text = apiData.mensagem,
                        sender = Sender.ASSISTANT
                    )

                    val offerMsgs = apiData.ofertas.map { offer ->
                        Message(
                            text = "",
                            sender = Sender.ASSISTANT,
                            isProduct = true,
                            productTitle = offer.produto,
                            price = offer.preco,
                            shop = offer.loja,
                            productUrl = offer.link
                        )
                    }

                    _messages.value += (listOf(textMsg) + offerMsgs)
                } else {
                    _messages.value += Message("Não entendi. Tente novamente.", Sender.ASSISTANT)
                }

            } catch (e: Exception) {
                // Remove loading em caso de erro também
                _messages.value = _messages.value.filterNot { it === loadingMessage }

                val errorMsg = when (e) {
                    is java.net.UnknownHostException -> "Sem conexão com a internet 📡"
                    is java.net.SocketTimeoutException -> "Tempo esgotado. Tente novamente ⏱️"
                    else -> "Erro ao buscar ofertas: ${e.localizedMessage}"
                }
                _messages.value += Message(errorMsg, Sender.ASSISTANT)
                e.printStackTrace()
            }
        }
    }

}
