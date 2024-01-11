package com.dawolf.yea.database.agent

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface AgentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(agent: Agent)

    @Query("select * from agent order by created_at desc")
    fun getAll(): LiveData<List<Agent>>

    @Query("select * from agent where rfid_no =:rfid")
    fun getAgent(rfid: String): LiveData<List<Agent>>

    @Query("delete from agent where rfid_no = :rfid")
    fun deleteAgent(rfid: String)

    @Query("delete from agent")
    fun deleteAll()

    //Delete
}