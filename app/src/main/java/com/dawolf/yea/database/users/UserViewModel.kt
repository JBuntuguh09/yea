package com.dawolf.yea.database.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionRepo
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutDao
import com.dawolf.yea.database.signout.SignoutRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application)  {

    val liveData : LiveData<List<Users>>

    private var repo : UsersRepo

    init {
        val sLogin = Maindb.getInstance(application).usersDao()
        repo = UsersRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(users: Users){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(users)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAll()
        }
    }
}