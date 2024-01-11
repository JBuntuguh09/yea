package com.dawolf.yea.database.region

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "region")
data class Region(
    @PrimaryKey
    val id: String,
    val name: String,
    val districts:String
)
