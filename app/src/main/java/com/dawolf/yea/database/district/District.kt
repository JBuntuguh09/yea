package com.dawolf.yea.database.district

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "district")
data class District(
    @PrimaryKey
    val id: String,
    val name: String
)
