package com.troot.assistentebuscapreco.model

data class Message(
    val text: String,
    val sender: Sender,
    val isProduct: Boolean = false,
    val productTitle: String? = null,
    val price: String? = null,
    val shop: String? = null,
    val productUrl: String? = null
)

enum class Sender {
    USER,
    ASSISTANT
}
