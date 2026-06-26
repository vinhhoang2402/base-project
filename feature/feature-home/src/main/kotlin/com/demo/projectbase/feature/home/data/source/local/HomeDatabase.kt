package com.demo.projectbase.feature.home.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.projectbase.feature.home.data.source.local.dao.MovieDao
import com.demo.projectbase.feature.home.data.source.local.dao.RemoteKeyDao
import com.demo.projectbase.feature.home.data.source.local.entity.MovieEntity
import com.demo.projectbase.feature.home.data.source.local.entity.RemoteKeyEntity

@Database(
    entities = [MovieEntity::class, RemoteKeyEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
