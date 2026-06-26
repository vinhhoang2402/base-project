package com.demo.projectbase.feature.home.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.projectbase.feature.home.data.source.local.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun count(): Int

    @Query("SELECT * FROM movies")
    fun pagingSource(): PagingSource<Int, MovieEntity>
}
