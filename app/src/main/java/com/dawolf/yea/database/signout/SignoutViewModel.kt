package com.dawolf.yea.database.signout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignoutViewModel (application: Application) : AndroidViewModel(application)  {

    val liveData : LiveData<List<Signout>>

    private var repo : SignoutRepo

    init {
        val sLogin = Maindb.getInstance(application).signoutDao()
        repo = SignoutRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(signout: Signout){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(signout)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAll()
        }
    }

    fun deleteByRfid(rfid: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteByRfid(rfid)
        }
    }
}