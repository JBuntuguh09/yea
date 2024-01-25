package com.dawolf.yea.database.attendance

import androidx.lifecycle.LiveData
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictDao

class AttendanceRepo(private val attendanceDao: AttendanceDao) {
    val liveData: LiveData<List<Attendance>> = attendanceDao.getAll()

    fun insert(attendance: Attendance){
        attendanceDao.insertAttendance(attendance)
    }


    fun deletebyRfid(rfid: String){
        attendanceDao.deleteAttendanceByRfid(rfid)
    }

    fun deleteAll(){
        attendanceDao.deleteAll()
    }

    fun getAllById(id:String) : LiveData<List<Attendance>>{
       return attendanceDao.getAllById(id)
    }

    fun deleteAllById(rfid: String, id: String){
        attendanceDao.deleteAttendanceByRfidId(rfid, id)
    }
}