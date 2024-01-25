package com.dawolf.yea.database.login

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LoginDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogin(login: Login)

    @Update
    fun updateLogin(login: Login)


    @Query("select * from login where username =:email and password =:password")
    fun getUser(email: String, password: String) : LiveData<List<Login>>

    @Query("select * from login")
    fun getUsers() : LiveData<List<Login>>


    @Query("delete from login where id =:id")
    fun deleteUser(id: String)


    @Query("delete from login")
    fun deleteUsers()

    @Query("update login set password = :pword, regionId= :regionId, districtId= :districtId, changedPassword= :passChanged where id = :userId")
    fun updateConfirm(pword: String, regionId: String, districtId:String, passChanged: String, userId: String)


}