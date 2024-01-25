package com.dawolf.yea.database.supervisor

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SupervisorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuper(supervisor: Supervisor)

    @Query("Select * from supervisor order by created_at desc")
    fun getAll(): LiveData<List<Supervisor>>

    @Query("Select * from supervisor where userId =:id")
    fun getSuper(id: String): LiveData<List<Supervisor>>

    @Query("Delete from supervisor where userId =:id")
    fun deleteSuper(id: String)

    @Query("update supervisor set name = :vName, phone=:vPhone, district_id=:vDistrict, district_name = :vDistrictName, region_id = :vRegion, " +
            "region_name=:vRegionName where supervisor_id=:superId")
    fun updateSupervisor(vName: String, vPhone: String, vRegionName:String, vRegion: String, vDistrictName:String, vDistrict: String,  superId: String)

}