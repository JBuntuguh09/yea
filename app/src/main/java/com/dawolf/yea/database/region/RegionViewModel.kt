package com.dawolf.yea.database.region

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dawolf.yea.database.Maindb
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegionViewModel(application: Application) : AndroidViewModel(application)  {

    val liveData : LiveData<List<Region>>

    private var repo : RegionRepo

    init {
        val sLogin = Maindb.getInstance(application).regionDao()
        repo = RegionRepo(sLogin)
        liveData = repo.liveData

    }

    fun insert(region: Region){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(region)
        }
    }
}