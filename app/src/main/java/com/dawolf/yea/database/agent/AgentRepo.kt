package com.dawolf.yea.database.agent

import androidx.lifecycle.LiveData

class AgentRepo(private val agentDao: AgentDao) {
    val liveData = agentDao.getAll()


    fun insert(agent: Agent){
        agentDao.insert(agent)
    }

    fun getAgent(rfid: String): LiveData<List<Agent>>{
        return agentDao.getAgent(rfid)
    }

    fun deleteAgent(rfid: String){
        agentDao.deleteAgent(rfid)
    }

    fun deleteAll(){
        agentDao.deleteAll()
    }
}