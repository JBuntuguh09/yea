package com.dawolf.yea.database.users

import androidx.lifecycle.LiveData
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionDao
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutDao

class UsersRepo(private val usersDao: UsersDao) {
    val liveData: LiveData<List<Users>> = usersDao.getAll()

    fun insert(users: Users){
        usersDao.insertUsers(users)
    }

    fun deleteAll(){
        usersDao.deleteAll()
    }
}