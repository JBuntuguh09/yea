package com.dawolf.yea.database.attendance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance", primaryKeys = ["rfid", "userId"])
data class Attendance(
    val rfid: String,
    val userId: String,
    val region_id: String,
    val district_id: String,
    val lat: String,
    val longi: String,
    val datetime: String,
    val sent : Boolean

)
