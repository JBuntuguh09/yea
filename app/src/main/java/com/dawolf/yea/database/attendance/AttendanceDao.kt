package com.dawolf.yea.database.attendance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttendance(attendance: Attendance)

    @Query("select * from attendance")
    fun getAll(): LiveData<List<Attendance>>

    @Query("select * from attendance where userId = :id")
    fun getAllById(id:String): LiveData<List<Attendance>>

    @Query("delete from attendance where rfid = :rfid")
    fun deleteAttendanceByRfid(rfid: String)
@Query("delete from attendance where rfid = :rfid and userId=:id")
    fun deleteAttendanceByRfidId(rfid: String, id:String)


    @Query("delete from attendance")
    fun deleteAll()
}