package com.dawolf.yea.database.agent

import androidx.lifecycle.LiveData

class AgentRepo(private val agentDao: AgentDao) {
    val liveData = agentDao.getAll()


    fun insert(agent: Agent){
        agentDao.insert(agent)
    }

    fun getAgent(id: String): LiveData<List<Agent>>{
        return agentDao.getAgent(id)
    }

    fun deleteAgent(id: String) : Int{
        return agentDao.deleteAgent(id)
    }

    fun deleteAll(){
        agentDao.deleteAll()
    }

    fun updateAgent(vName:String, vPhone : String, vDob: String, vGender:String, vAddress:String, vDistrictName: String, vDistrict: String, vRegionName: String, vRegion: String,
                    vLat: String, vLong: String, agentId:String){
        agentDao.updateAgent(vName, vPhone , vDob, vGender, vAddress, vDistrictName,vDistrict, vRegionName,vRegion, vLat, vLong, agentId)
    }
}