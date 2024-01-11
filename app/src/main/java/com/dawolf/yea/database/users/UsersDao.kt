package com.dawolf.yea.database.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: Users)

    @Query("select * from users order by created_at desc")
    fun getAll(): LiveData<List<Users>>

    @Query("delete from  users ")
    fun deleteAll()
}