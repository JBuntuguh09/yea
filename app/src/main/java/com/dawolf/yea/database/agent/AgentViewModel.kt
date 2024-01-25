package com.dawolf.yea.database.agent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgentViewModel(application: Application) : AndroidViewModel(application) {

    val liveData : LiveData<List<Agent>>

    private var repo : AgentRepo

    init {
        val sLogin = Maindb.getInstance(application).agentDao()
        repo = AgentRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(agent: Agent){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(agent)
        }
    }

    fun getAgent(rfid: String): LiveData<List<Agent>> {
//        viewModelScope.launch(Dispatchers.IO) {
//            return@launch repo.getUser(email, password)
//        }
        return repo.getAgent(rfid)
    }

    fun deleteAll(id: String){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAll()
        }
    }

    fun deleteAgent(rfid: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAgent(rfid)
        }
    }

    fun updateAgent(vName:String, vPhone : String, vDob: String, vGender:String, vAddress:String,vDistrictName: String, vDistrict: String, vRegionName: String, vRegion: String,
                    vLat: String, vLong: String, agentId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAgent(vName, vPhone , vDob, vGender, vAddress, vDistrictName, vDistrict, vRegionName, vRegion, vLat, vLong, agentId)
        }
    }
}