package com.troot.assistentebuscapreco.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface que define os endpoints da API para o Retrofit.
 */
interface ApiService {

    /**
     * Envia uma consulta de busca para a API e espera uma lista de ofertas de produtos.
     *
     * @param request O corpo da requisição, contendo a query de busca.
     * @return Uma lista de objetos ProductOffer encapsulados em um objeto Response.
     */
    @POST("buscar")
    suspend fun searchOffers(
        @Body request: SearchRequest
    ): Response<ChatResponse>
}
