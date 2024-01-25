package com.dawolf.yea.database.signout

import androidx.lifecycle.LiveData

class SignoutRepo(private val signoutDao: SignoutDao) {
    val liveData: LiveData<List<Signout>> = signoutDao.getAll()

    fun insert(signout: Signout){
        signoutDao.insertSignout(signout)
    }

    fun deleteByRfid(rfid: String){
        signoutDao.deleteByRfid(rfid)
    }

    fun deleteByRfidId(rfid: String, id: String){
        signoutDao.deleteByRfidId(rfid, id)
    }

    fun deleteAll(){
        signoutDao.deleteAll()
    }

    fun getAllById(id:String) : LiveData<List<Signout>>{
        return signoutDao.getAllNyId(id)
    }
}