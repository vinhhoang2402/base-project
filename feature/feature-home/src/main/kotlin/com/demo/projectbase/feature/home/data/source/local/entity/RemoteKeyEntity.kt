package com.demo.projectbase.feature.home.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val label: String,
    val nextPage: Int?,
)
