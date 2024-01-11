package com.dawolf.yea.database.agent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent")
data class Agent(
    @PrimaryKey
    val rfid_no : String,
    val id : String,
    val agent_id: String,
    val name: String,
    val dob: String,
    val phone: String,
    val address: String,
    val region_name: String,
    val region_id: String,
    val district_name: String,
    val district_id: String,
    val longitude: String,
    val latitude: String,
    val supervisor_name: String,
    val supervisor_id: String,
    val supervisor_email: String,
    val supervisor_phone: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val gender: String


)
