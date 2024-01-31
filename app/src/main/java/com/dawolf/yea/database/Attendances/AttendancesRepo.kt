package com.dawolf.yea.database.Attendances

import androidx.lifecycle.LiveData
import com.dawolf.yea.database.attendance.Attendance

class AttendancesRepo(private val attendancesDao: AttendancesDao) {
    val liveData: LiveData<List<Attendances>> = attendancesDao.getAll()

    fun insert(attendances: Attendances){
        attendancesDao.insertAttendances(attendances)
    }

    fun getAttendancesById(id: String): LiveData<List<Attendances>>{
        return attendancesDao.getAttendById(id)
    }

    fun deleteAttendance(id: String){
        attendancesDao.deleteAttendances(id)
    }

    fun deletebyRfid(rfid: String){
        attendancesDao.deleteAttendanceByRfid(rfid)
    }

    fun deleteAll(){
        attendancesDao.deleteAll()
    }
}