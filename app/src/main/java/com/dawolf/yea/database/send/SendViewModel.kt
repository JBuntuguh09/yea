package com.dawolf.yea.database.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendViewModel(application: Application) : AndroidViewModel(application)  {

    val liveDataAttend : LiveData<List<Send>>
    val liveDataSignout : LiveData<List<Send>>
    val liveData: LiveData<List<Send>>

    private var repo : SendRepo

    init {
        val sLogin = Maindb.getInstance(application).sendDao()
        repo = SendRepo(sLogin)
        liveDataAttend = repo.liveDataAttend
        liveDataSignout = repo.liveDataSignout
        liveData = repo.liveData

    }

    fun insert(send: Send){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(send)
        }
    }

    fun deleteSendAttendById(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAttendById(id)
        }
    }

    fun deleteSendSignoutById(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSignoutById(id)
        }
    }

    fun deleteSendAttend(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAttend()
        }
    }

    fun deleteSendSignout(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSignou()
        }
    }

    fun updateSend(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateSend(id)
        }
    }

    fun updateSendFailed(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateSendFailed(id)
        }
    }
}