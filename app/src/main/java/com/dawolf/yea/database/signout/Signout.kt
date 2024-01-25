package com.dawolf.yea.database.signout

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signout", primaryKeys = ["rfid_no", "userId"])
data class Signout(

    val rfid_no: String,
    val userId: String,
    val signout_date: String,
    val sent : Boolean
)