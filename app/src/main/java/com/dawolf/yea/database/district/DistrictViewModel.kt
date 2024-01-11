package com.dawolf.yea.database.district

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.login.Login
import com.dawolf.yea.database.login.LoginRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DistrictViewModel(application: Application) : AndroidViewModel(application)  {
    val liveData : LiveData<List<District>>

    private var repo : DistrictRepo

    init {
        val sLogin = Maindb.getInstance(application).districtDao()
        repo = DistrictRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(district: District){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(district)
        }
    }


}