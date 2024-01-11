package com.dawolf.yea.database.send

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "send")
data class Send(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val rfid: String,
    val type: String,
    val signout_date: String,
    val region_id: String,
    val district_id: String,
    val status: String = "Unsent"

)
