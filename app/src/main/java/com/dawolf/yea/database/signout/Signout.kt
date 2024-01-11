package com.dawolf.yea.database.signout

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signout")
data class Signout(

    @PrimaryKey
    val rfid_no: String,
    val signout_date: String,
    val sent : Boolean
)