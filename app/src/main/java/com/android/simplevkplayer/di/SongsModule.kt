package com.android.simplevkplayer.di

import com.android.simplevkplayer.data.player.SongsRepositoryImpl
import com.android.simplevkplayer.domain.SongsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SongsModule {

    //    @Provides
//    fun provideRepository(): SongsRepository = SongsRepository()


}

@Module
@InstallIn(SingletonComponent::class)
interface SongsModuleInterface {
    @Binds
    @Singleton
    fun bindSongsRepository(impl: SongsRepositoryImpl): SongsRepository
}