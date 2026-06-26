package com.demo.projectbase.feature.home.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.projectbase.feature.home.data.source.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(key: RemoteKeyEntity)

    @Query("SELECT * FROM remote_keys WHERE label = :label")
    suspend fun getKey(label: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAll()
}
