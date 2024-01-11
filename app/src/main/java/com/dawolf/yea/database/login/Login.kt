package com.dawolf.yea.database.login

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login")
data class Login(
    @PrimaryKey
    val id: String,
    val token :String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val dob: String,
    val regionId: String,
    val districtId: String,
    val changedPassword: String,
    val emailVerified: String,
    val username: String
)
