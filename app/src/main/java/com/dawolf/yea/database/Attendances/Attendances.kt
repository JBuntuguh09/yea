package com.dawolf.yea.database.Attendances

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendances", primaryKeys = ["id", "userId"])
data class Attendances(
    val id: String,
    val userId: String,
    val rfid_id: String,
    val region_name: String,
    val region_id: String,
    val district_name: String,
    val district_id: String,
    val supervisor_name: String,
    val supervisor_id: String,
    val agent_name: String,
    val agent_id: String,
    val signout_date: String,
    val signout_by: String,
    val created_at: String,
)
