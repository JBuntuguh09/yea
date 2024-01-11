package com.dawolf.yea.database.signout

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SignoutDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSignout(signout: Signout)

    @Query("select * from signout")
    fun getAll(): LiveData<List<Signout>>

    @Query("delete from signout where rfid_no = :rfid")
    fun deleteByRfid(rfid:String)

    @Query("delete from signout")
    fun deleteAll()
}