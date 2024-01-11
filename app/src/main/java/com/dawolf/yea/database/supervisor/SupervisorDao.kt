package com.dawolf.yea.database.supervisor

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SupervisorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuper(supervisor: Supervisor)

    @Query("Select * from supervisor order by created_at desc")
    fun getAll(): LiveData<List<Supervisor>>

    @Query("Select * from supervisor where supervisor_id =:id")
    fun getSuper(id: String): LiveData<List<Supervisor>>

    @Query("Delete from supervisor where supervisor_id =:id")
    fun deleteSuper(id: String)

}