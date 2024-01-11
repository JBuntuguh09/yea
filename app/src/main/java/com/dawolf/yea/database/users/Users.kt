package com.dawolf.yea.database.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class Users(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val districtId: String,
    val regionId: String,
    val created_at: String
)
