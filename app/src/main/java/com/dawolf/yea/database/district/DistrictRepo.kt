package com.dawolf.yea.database.district

import androidx.lifecycle.LiveData

class DistrictRepo(private val districtDao: DistrictDao) {
    val liveData: LiveData<List<District>> = districtDao.getAll()

    fun insert(district: District){
        districtDao.insertDistrict(district)
    }


}