package com.dawolf.yea.database.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val liveData : LiveData<List<Login>>

    private var repo : LoginRepo

    init {
        val sLogin = Maindb.getInstance(application).loginDao()
        repo = LoginRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(login: Login){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(login)
        }
    }

    fun getUser(email:String, password:String):LiveData<List<Login>>{
//        viewModelScope.launch(Dispatchers.IO) {
//            return@launch repo.getUser(email, password)
//        }
        return repo.getUser(email, password)
    }

    fun deleteUser(id: String){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteUser(id)
        }
    }
}