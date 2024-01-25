package com.dawolf.yea.database.Attendances

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendancesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttendances(attendances: Attendances)

    @Query("select * from attendances order by created_at desc")
    fun getAll(): LiveData<List<Attendances>>

    @Query("select * from attendances where userId =:userId order by created_at desc")
    fun getAttendById(userId: String): LiveData<List<Attendances>>

    @Query("delete from attendances where rfid_id = :rfid")
    fun deleteAttendanceByRfid(rfid: String)

    @Query("delete from attendances where userId = :id")
    fun deleteAttendances(id: String)

    @Query("delete from  attendances")
    fun deleteAll()
}