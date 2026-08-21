package com.inttelgo.tecnicos.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.inttelgo.tecnicos.logic.Model.LoginRequest
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

@SuppressLint("StaticFieldLeak")
object RetrofitClient {
    //private const val BASE_URL = "http://192.168.1.23:3000/"

    private const val BASE_URL =  "https://api.inttelgo.com/"
    private const val TAG = "RetrofitClient"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    // Se inicializan con valores por defecto
    private var token: String? = null
    private var context: Context? = null
    private var httpClient: OkHttpClient = buildClient()
    private var retrofit: Retrofit = buildRetrofit(httpClient)

    var api: ApiService = retrofit.create(ApiService::class.java)
        private set

    // Inicializar con contexto para poder acceder a UserPreferences
    fun initialize(context: Context) {
        this.context = context
        loadTokenFromPreferences(context)
    }

    private fun loadTokenFromPreferences(context: Context) {
        val userPreferences = UserPreferences(context)
        val savedToken = userPreferences.getToken()
        if (savedToken != null && userPreferences.isTokenValid()) {
            token = savedToken
            rebuildClient()
        }
    }

    private fun buildClient(): OkHttpClient {
        // TrustManager que acepta todos los certificados (SOLO PARA DESARROLLO)
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        return OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(300, TimeUnit.SECONDS)
            writeTimeout(300, TimeUnit.SECONDS)
            callTimeout(420, TimeUnit.SECONDS)
            addInterceptor(logging)
            addInterceptor(AuthInterceptor())
            addInterceptor(TokenRefreshInterceptor())
            // SOLO PARA DESARROLLO: Aceptar todos los certificados SSL
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { _, _ -> true } // Aceptar cualquier hostname
        }.build()
    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Interceptor para agregar el token a las peticiones
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder().apply {
                token?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }.build()
            return chain.proceed(newRequest)
        }
    }

    // Interceptor para renovar token automáticamente cuando expire
    private class TokenRefreshInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            // Si recibimos un 401 o 403, intentar renovar el token
            if ((response.code == 401 || response.code == 403) && context != null) {
                val userPreferences = UserPreferences(context!!)

                if (userPreferences.hasSavedCredentials()) {
                    val username = userPreferences.getSavedUsername()
                    val password = userPreferences.getSavedPassword()

                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        // Intentar hacer login automáticamente
                        val refreshed = runBlocking {
                            try {
                                val loginRequest = LoginRequest(username, password)
                                val loginResponse = api.login(loginRequest)

                                if (loginResponse.isSuccessful && loginResponse.body()?.success == true) {
                                    val newToken = loginResponse.body()!!.data!!.token
                                    updateAuthToken(newToken, context!!)
                                    userPreferences.saveToken(newToken)
                                    true
                                } else {
                                    false
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error al renovar token: ${e.message}")
                                false
                            }
                        }

                        if (refreshed) {
                            // Reintentar la petición original con el nuevo token
                            val newRequest = request.newBuilder()
                                .header("Authorization", "Bearer $token")
                                .build()
                            response.close()
                            return chain.proceed(newRequest)
                        }
                    }
                }
            }

            return response
        }
    }

    fun updateAuthToken(newToken: String, context: Context? = null) {
        token = newToken
        context?.let {
            UserPreferences(it).saveToken(newToken)
        }
        rebuildClient()
    }

    private fun rebuildClient() {
        httpClient = buildClient()
        retrofit = buildRetrofit(httpClient)
        api = retrofit.create(ApiService::class.java)
    }
}