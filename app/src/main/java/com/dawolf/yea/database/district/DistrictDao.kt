package com.dawolf.yea.database.district

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DistrictDao {

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insertDistrict(district: District )

    @Query("Select * from  district")
    fun getAll():LiveData<List<District>>


}