package com.troot.assistentebuscapreco.network

import com.google.gson.annotations.SerializedName

// Objeto que o Android envia para a API
data class SearchRequest(
    @SerializedName("query") val query: String
)

// Objeto que representa um único produto na resposta da API
data class ProductOffer(
    @SerializedName("produto") val produto: String,
    @SerializedName("preco") val preco: String,
    @SerializedName("loja") val loja: String,
    @SerializedName("link") val link: String
)
