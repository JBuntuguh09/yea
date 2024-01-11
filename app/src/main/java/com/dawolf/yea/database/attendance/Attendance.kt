package com.dawolf.yea.database.attendance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey
    val rfid: String,
    val region_id: String,
    val district_id: String,
    val datetime: String,
    val sent : Boolean

)
