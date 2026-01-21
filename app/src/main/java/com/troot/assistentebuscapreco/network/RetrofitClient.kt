package com.troot.assistentebuscapreco.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Objeto singleton que fornece uma instância configurada do Retrofit.
 */
object RetrofitClient {

    // URL base da sua API local.
    // 10.0.2.2 é o endereço especial que o emulador Android usa para se referir
    // ao localhost (127.0.0.1) da máquina hospedeira.
    private const val BASE_URL = "http://192.168.1.7:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // 'BODY' mostra tudo: headers, url e o JSON (request e response)
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS) // Tempo para conectar ao servidor
        .readTimeout(120, TimeUnit.SECONDS)    // Tempo esperando a resposta (o mais importante aqui)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Criação da instância do Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para converter JSON
            .build()
    }

    // Fornece uma instância pronta do nosso ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
