package com.dawolf.yea.database.agent

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dawolf.yea.database.district.District

@Dao
interface AgentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(agent: Agent)

    @Query("select * from agent order by created_at desc")
    fun getAll(): LiveData<List<Agent>>

    @Query("select * from agent where userId =:id order by created_at desc")
    fun getAgent(id: String): LiveData<List<Agent>>

    @Query("delete from agent where userId = :id")
    fun deleteAgent(id: String)

    @Query("delete from agent")
    fun deleteAll()

    @Query("update agent set name = :vName, phone=:vPhone, dob= :vDob, gender=:vGender, " +
            "address=:vAddress, district_name = :vDistrictName, district_id=:vDistrict, region_name=:vRegionName, region_id = :vRegion, latitude=:vLat, longitude=:vLong where agent_id = :agentId")
    fun updateAgent(vName:String, vPhone : String, vDob: String, vGender:String, vAddress:String, vDistrictName: String, vDistrict: String, vRegionName: String, vRegion: String,
                    vLat: String, vLong: String, agentId:String)

    //Delete
}