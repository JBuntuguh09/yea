package com.dawolf.yea.database.supervisor

import androidx.lifecycle.LiveData

class SupervisorRepo(private val supervisorDao: SupervisorDao) {

    val liveData : LiveData<List<Supervisor>> = supervisorDao.getAll()

    fun insert(supervisor: Supervisor){
        supervisorDao.insertSuper(supervisor)
    }

    fun getSuper(id: String): LiveData<List<Supervisor>>{
       return supervisorDao.getSuper(id)
    }

    fun deleteSuper(id: String){
        supervisorDao.deleteSuper(id)
    }
}