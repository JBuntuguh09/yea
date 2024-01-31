package com.dawolf.yea.database.send

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SendDao {

    @Insert
    fun insertSend(send: Send)

    @Query("select * from send where type = 'attendance' and status = 'Unsent'")
    fun getSendAttend():LiveData<List<Send>>

    @Query("select * from send where type = 'signout' and status = 'Unsent'")
    fun getSendSignout():LiveData<List<Send>>

    @Query("select * from send  where  status = 'Unsent'")
    fun getAll():LiveData<List<Send>>

    @Query("delete from send where id =:id")
    fun deleteSendAttendById(id: String)


    @Query("delete from send where id =:id")
    fun deleteSendSignoutById(id: String)

    @Query("delete from send where type = 'attendance'")
    fun deleteSendAttend()

    @Query("delete from send where type = 'signout'")
    fun deleteSendSignout()

    @Query("update send set status='Sent' where id =:id")
    fun updateSend(id:String)


    @Query("update send set status='Failed' where id =:id")
    fun updateSendFailed(id:String)



}