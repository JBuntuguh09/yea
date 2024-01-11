package com.dawolf.yea.database.Attendances

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.attendance.AttendanceRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendancesViewModel (application: Application): AndroidViewModel(application) {
    val liveData : LiveData<List<Attendances>>

    private var repo : AttendancesRepo

    init {
        val sLogin = Maindb.getInstance(application).attendancesDao()
        repo = AttendancesRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(attendances: Attendances){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(attendances)
        }
    }

    fun deleteBid(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAttendance(id)
        }
    }

    fun deleteByRfid(rfid: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletebyRfid(rfid)
        }
    }

    fun deleteAny(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAll()
        }
    }
}