package com.dawolf.yea.database.login

import androidx.lifecycle.LiveData

class LoginRepo(private val loginDao: LoginDao) {
    val liveData: LiveData<List<Login>> = loginDao.getUsers()


    fun insert(login: Login){
        loginDao.insertLogin(login)
    }

    fun getUser(email: String, password: String) :LiveData<List<Login>>{
        return loginDao.getUser(email, password)


    }

    fun getUsers(){
        loginDao.getUsers()
    }

    fun deleteUser(id: String){
        loginDao.deleteUser(id)
    }

    fun deleteUsers(){
        loginDao.deleteUsers()
    }
}