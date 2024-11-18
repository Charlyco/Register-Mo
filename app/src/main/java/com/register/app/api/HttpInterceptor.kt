package com.register.app.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.register.app.util.DataStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.EMPTY_RESPONSE
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class HttpInterceptor @Inject constructor(@ApplicationContext val context: Context,
    private val dataStoreManager: DataStoreManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken: String? = runBlocking {dataStoreManager.readTokenData()}
        return try {
            val originalRequest = chain.request()
            val request = if (authToken != null && authToken.length > 2) {
                Log.i("Token","Adding Authorization header with token: $authToken")
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
            } else {
                Log.i("Token", "No token provided, proceeding with the original request.")
                originalRequest
            }
            chain.proceed(request)
        }catch (e: IOException) {
            if (e is SocketTimeoutException) {
                showToast("Request timed out, try again")
            }
            return Response.Builder()
                .message("An error has occurred")
                .code(500)
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .body(EMPTY_RESPONSE)
                .build()
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}