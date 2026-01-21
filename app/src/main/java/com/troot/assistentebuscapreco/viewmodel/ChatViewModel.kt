package com.troot.assistentebuscapreco.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troot.assistentebuscapreco.data.local.ProfilePrefs
import com.troot.assistentebuscapreco.model.Message
import com.troot.assistentebuscapreco.model.Sender
import com.troot.assistentebuscapreco.network.RetrofitClient
import com.troot.assistentebuscapreco.network.SearchRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    // garante que a mensagem inicial não repete
    private var didInitWelcome = false

    // Chame uma vez pela MainActivity
    fun initialize(context: Context) {
        if (didInitWelcome) return
        didInitWelcome = true
        showWelcomeMessage(context.applicationContext)
    }

    private fun showWelcomeMessage(appContext: Context) {
        viewModelScope.launch {
            val prefs = ProfilePrefs(appContext)
            val name = prefs.userName.trim().ifBlank { "por aí" }

            val firstPreference = prefs.preferencesCsv
                .split(",")
                .map { it.trim() }
                .firstOrNull { it.isNotBlank() }

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = when (hour) {
                in 6..11 -> "Bom dia"
                in 12..17 -> "Boa tarde"
                else -> "Boa noite"
            }

            val preferenceLine = if (!firstPreference.isNullOrBlank()) {
                "Quer começar com ofertas de **$firstPreference**?"
            } else {
                "Quer começar buscando ofertas de qual produto?"
            }

            val welcomeText = """$greeting, $name! 👋  
Sou o **Assistente Busca Preço**.

Eu consigo fazer duas coisas:

**1) Buscar ofertas e preços**
Diga o que você quer + se tiver, coloque limite de preço.
**Exemplos:**
* "Ryzen 5 5500"
* "Notebook Asus TUF Gamer"
* "iPhone 15"

**2) Conversar sobre um produto**
Se você quiser entender melhor antes de comprar:
**Exemplos:**
* "vale a pena o iPhone 15 hoje?"
* "qual a diferença entre Ryzen 5 5500 e 5600?"
* "esse notebook é bom pra estudar?"

$preferenceLine 🛍️"""

            // Mostra "Digitando..."
            val typingMsg = Message(text = "Digitando...", sender = Sender.ASSISTANT)
            _messages.value = listOf(typingMsg)

            delay(1200)

            // Substitui pelo texto final
            _messages.value = listOf(
                Message(text = welcomeText, sender = Sender.ASSISTANT)
            )
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
