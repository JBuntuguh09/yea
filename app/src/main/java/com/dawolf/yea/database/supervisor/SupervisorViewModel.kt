package com.dawolf.yea.database.supervisor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.login.Login
import com.dawolf.yea.database.login.LoginRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupervisorViewModel(application: Application) : AndroidViewModel(application)  {

    val liveData : LiveData<List<Supervisor>>

    private var repo : SupervisorRepo

    init {
        val sLogin = Maindb.getInstance(application).supervisorDao()
        repo = SupervisorRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(supervisor: Supervisor){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(supervisor)
        }
    }

    fun getSuper(id: String): LiveData<List<Supervisor>> {

        return repo.getSuper(id)
    }

    fun deleteSuper(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSuper(id)
        }
    }

    fun updateSupervisor(vName: String, vPhone: String, vRegionName:String, vRegion: String, vDistrictName:String, vDistrict: String,  superId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateSupervisor(vName, vPhone, vRegionName, vRegion, vDistrictName, vDistrict, superId)
        }
    }
}