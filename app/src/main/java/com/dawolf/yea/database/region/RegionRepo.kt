package com.dawolf.yea.database.region

import androidx.lifecycle.LiveData

class RegionRepo(private val regionDao: RegionDao) {
    val liveData: LiveData<List<Region>> = regionDao.getAll()

    fun insert(region: Region){
        regionDao.insertRegion(region)
    }
}