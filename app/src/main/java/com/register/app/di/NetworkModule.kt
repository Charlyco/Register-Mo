package com.register.app.di

import android.content.Context
import com.register.app.api.ChatService
import com.register.app.api.GroupService
import com.register.app.api.HttpInterceptor
import com.register.app.api.UserService
import com.register.app.util.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val logger: HttpLoggingInterceptor =

        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    //OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context, dataStoreManager: DataStoreManager): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpInterceptor(context, dataStoreManager))
            .addInterceptor(logger)
            .callTimeout(Duration.ofMillis(100000))
            .readTimeout(Duration.ofMillis(100000))
            .retryOnConnectionFailure(true)
            .build()
        return Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun providesGroupService(retrofit: Retrofit) : GroupService {
        return retrofit.create(GroupService::class.java)
    }

    @Provides
    fun providesUserService(retrofit: Retrofit) : UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun providesChatService(retrofit: Retrofit) : ChatService {
        return retrofit.create(ChatService::class.java)
    }
}