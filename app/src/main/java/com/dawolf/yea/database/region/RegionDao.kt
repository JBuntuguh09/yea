package com.dawolf.yea.database.region

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dawolf.yea.database.district.District

@Dao
interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegion(region: Region)

    @Query("Select * from  region order by name desc")
    fun getAll(): LiveData<List<Region>>

}