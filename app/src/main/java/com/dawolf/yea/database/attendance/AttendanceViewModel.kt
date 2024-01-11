package com.dawolf.yea.database.attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application): AndroidViewModel(application) {
    val liveData : LiveData<List<Attendance>>

    private var repo : AttendanceRepo

    init {
        val sLogin = Maindb.getInstance(application).attendanceDao()
        repo = AttendanceRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(attendance: Attendance){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(attendance)
        }
    }

    fun deleteByRfid(rfid: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletebyRfid(rfid)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAll()
        }
    }
}