package com.register.app.di

import com.register.app.repository.ActivityRepository
import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.repository.GroupRepository
import com.register.app.repository.NotificationRepository
import com.register.app.repository.QuestionnaireRepository
import com.register.app.repositoryimpls.ActivityRepositoryImpl
import com.register.app.repositoryimpls.AuthRepositoryImpl
import com.register.app.repositoryimpls.ChatRepositoryImpl
import com.register.app.repositoryimpls.GroupRepositoryImpl
import com.register.app.repositoryimpls.NotificationRepositoryImpl
import com.register.app.repositoryimpls.QuestionnaireRepositoryImpl
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
    abstract fun bindsGroupRepository(groupRepositoryImpl: GroupRepositoryImpl): GroupRepository

    @Singleton
    @Binds
    abstract fun bindsChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Singleton
    @Binds
    abstract fun bindsActivityRepository(activityRepositoryImpl: ActivityRepositoryImpl): ActivityRepository

    @Singleton
    @Binds
    abstract fun bindsNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Singleton
    @Binds
    abstract fun bindsQuestionnaireRepository(questionnaireRepositoryImpl: QuestionnaireRepositoryImpl): QuestionnaireRepository
}