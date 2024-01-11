package com.dawolf.yea.database.send

import androidx.lifecycle.LiveData
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionDao

class SendRepo(private val sendDao: SendDao) {
    val liveDataAttend: LiveData<List<Send>> = sendDao.getSendAttend()
    val liveDataSignout: LiveData<List<Send>> = sendDao.getSendSignout()

    fun insert(send: Send){
        sendDao.insertSend(send)
    }

    fun deleteAttendById(id: String){
        sendDao.deleteSendAttendById(id)
    }

    fun deleteSignoutById(id: String){
        sendDao.deleteSendSignoutById(id)
    }

    fun deleteAttend(){
        sendDao.deleteSendAttend()
    }

    fun deleteSignou(){
        sendDao.deleteSendSignout()
    }

    fun updateSend(id: String){
        sendDao.updateSend(id)
    }


}