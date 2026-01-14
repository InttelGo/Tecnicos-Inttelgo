package com.inttelgo.tecnicos.logic.repository

import com.inttelgo.tecnicos.network.RetrofitClient

class HomeRepository {

    suspend fun consultAllNeighborhoods () = RetrofitClient.api.consultAllNeighborhoods()
}