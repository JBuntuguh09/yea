package com.dawolf.yea.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dawolf.yea.database.Attendances.Attendances
import com.dawolf.yea.database.Attendances.AttendancesDao
import com.dawolf.yea.database.agent.Agent
import com.dawolf.yea.database.agent.AgentDao
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.attendance.AttendanceDao
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictDao
import com.dawolf.yea.database.login.Login
import com.dawolf.yea.database.login.LoginDao
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionDao
import com.dawolf.yea.database.send.Send
import com.dawolf.yea.database.send.SendDao
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutDao
import com.dawolf.yea.database.supervisor.Supervisor
import com.dawolf.yea.database.supervisor.SupervisorDao
import com.dawolf.yea.database.users.Users
import com.dawolf.yea.database.users.UsersDao


@Database(entities = [Login::class, Agent::class, District::class, Supervisor::class, Region::class, Attendance::class, Attendances::class,
                     Signout::class, Users::class, Send::class], version = 16)
abstract class Maindb: RoomDatabase() {

    abstract fun loginDao(): LoginDao
    abstract fun  agentDao(): AgentDao
    abstract fun  regionDao(): RegionDao
    abstract fun  districtDao(): DistrictDao
    abstract fun  supervisorDao(): SupervisorDao
    abstract fun  attendanceDao(): AttendanceDao
    abstract fun  attendancesDao(): AttendancesDao
    abstract fun  signoutDao(): SignoutDao
    abstract fun  usersDao(): UsersDao
    abstract fun  sendDao(): SendDao

    companion object {
        @Volatile
        var instance : Maindb? = null

        fun getInstance(context: Context): Maindb {
            val tempInstance = instance
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this) {
                val nInstance = Room.databaseBuilder(
                    context.applicationContext,
                    Maindb::class.java,
                    "yea_db"
                ).fallbackToDestructiveMigration()
                    .build()
                instance = nInstance
                return nInstance
            }


        }
    }
}