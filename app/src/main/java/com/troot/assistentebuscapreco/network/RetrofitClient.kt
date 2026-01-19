package com.troot.assistentebuscapreco.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que fornece uma instância configurada do Retrofit.
 */
object RetrofitClient {

    // URL base da sua API local.
    // 10.0.2.2 é o endereço especial que o emulador Android usa para se referir
    // ao localhost (127.0.0.1) da máquina hospedeira.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Criação da instância do Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para converter JSON
            .build()
    }

    // Fornece uma instância pronta do nosso ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
