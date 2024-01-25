package com.dawolf.yea.database.supervisor

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supervisor", primaryKeys = ["supervisor_id", "userId"])
data class Supervisor(
    val supervisor_id: String,
    val userId: String,
    val id: String,
    val name: String,
    val phone: String,
    val status: String,
    val region_name: String,
    val region_id: String,
    val district_name: String,
    val district_id: String,
    val created_at: String,

)
