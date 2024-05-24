package com.register.app.di

import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.repository.GroupRepository
import com.register.app.repositoryimpls.AuthRepositoryImpl
import com.register.app.repositoryimpls.ChatRepositoryImpl
import com.register.app.repositoryimpls.GroupRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindsGroupRepository(eventRepositoryImpl: GroupRepositoryImpl): GroupRepository

    @Singleton
    @Binds
    abstract fun bindsChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
}