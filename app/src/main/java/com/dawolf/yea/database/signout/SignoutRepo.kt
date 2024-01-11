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

    fun deleteAll(){
        signoutDao.deleteAll()
    }
}