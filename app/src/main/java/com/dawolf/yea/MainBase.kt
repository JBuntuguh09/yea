package com.dawolf.yea

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictViewModel
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionViewModel
import com.dawolf.yea.database.send.SendViewModel
import com.dawolf.yea.database.signout.SignoutViewModel
import com.dawolf.yea.databinding.ActivityMainBaseBinding
import com.dawolf.yea.fragments.StartPage
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainBase : AppCompatActivity() {
     lateinit var binding: ActivityMainBaseBinding
    private lateinit var storage: Storage
     var arrayListAgent = HashMap<String, String>()
     var arrayListSuper = HashMap<String, String>()
    var arrayListAttend = HashMap<String, String>()

    lateinit var regionViewModel: RegionViewModel
    lateinit var districtViewModel: DistrictViewModel
    var agentRFID = ""
    var superId = ""
    var attendanceId = ""
    lateinit var sendViewModel: SendViewModel
    lateinit var attendanceViewModel: AttendanceViewModel
    lateinit var signoutViewModel: SignoutViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_base)
        binding = ActivityMainBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Storage(this)

        regionViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[RegionViewModel::class.java]
        districtViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[DistrictViewModel::class.java]
        sendViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SendViewModel::class.java]
        attendanceViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[AttendanceViewModel::class.java]
        signoutViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SignoutViewModel::class.java]


        getRegion()
        getDistrict()
        navTo(StartPage(), "Welcome", "Login", 0)
        getWatchers()

    }

    fun getWatchers(){
        sendViewModel.liveDataAttend.observe(this){data->
            if(data.isNotEmpty()){
                try {
                    println("here we go  "+ data)
                    for (a in data.indices){
                        sendAttendance(data[a].id.toString(), data[a].rfid, data[a].region_id, data[a].district_id)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

        sendViewModel.liveDataSignout.observe(this){data->
            if(data.isNotEmpty()){
                try {
                    println("bee we go $data")
                    for (a in data.indices){
                        senDSign(data[a].id.toString(), data[a].rfid, data[a].signout_date)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun senDSign(id: String, rfid: String, signoutDate: String) {
        val api = API()
        val body = mapOf(
            "signout_date" to signoutDate

        )

        try {
            GlobalScope.launch {
                val res:String = api.postAPIWithHeader(
                    Constant.URL + "api/signout/update/${rfid}",
                    body,
                    this@MainBase
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){
                        // Toast.makeText(this@Startpage, "$rfid failed to create attendance", Toast.LENGTH_SHORT).show()

                    }else {
                        setSignInfo(res, id, rfid)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this@MainBase, "Error: Failed to signout", Toast.LENGTH_SHORT).show()



        }

    }

    private fun setSignInfo(res: String, id: String, rfid: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

            // Toast.makeText(this@MainBase, mess, Toast.LENGTH_SHORT).show()
            signoutViewModel.deleteByRfid(rfid)
            sendViewModel.updateSend(id)

        }catch (e: Exception){
            e.printStackTrace()

        }
    }


    fun navTo(frag: Fragment, page: String, prev: String, returnable: Int) {
        binding.txtTopic.text = page
        storage.currPage = page
        storage.fragValPrev = prev

        if(returnable==3){
            val fragmentManagers = supportFragmentManager
            fragmentManagers.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        when (returnable) {
            1 -> {
                fragmentTransaction.replace(R.id.frameMain, frag, page).addToBackStack(page)
            }
            2 -> {
                fragmentTransaction.replace(R.id.frameMain, frag, page).addToBackStack(prev)
            }
            3 -> {
                fragmentTransaction.replace(R.id.frameMain, frag).addToBackStack("prev")
            }
            else -> {
                fragmentTransaction.replace(R.id.frameMain, frag, page)
            }
        }

        fragmentTransaction.commit()

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getRegion(){

        binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/regions-districts",  this@MainBase)
                withContext(Dispatchers.Main){

                    setRegionInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainBase, "No regions found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setRegionInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            var data = jsonObject.getJSONArray("data")
            val list = ArrayList<String>()
            list.add("Select region")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()
                hash["id"] = jObject.optString("id")
                hash["name"] = jObject.optString("name")
                hash["districts"] = jObject.getJSONArray("districts").toString()

                val region = Region(jObject.optString("id"), jObject.optString("name"), jObject.getJSONArray("districts").toString())
                regionViewModel.insert(region)

            }


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getDistrict(){

        binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/districts",  this@MainBase)
                withContext(Dispatchers.Main){
                    setDistrictInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainBase, "No data found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setDistrictInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            val list = ArrayList<String>()
            list.add("Select districts")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()
                hash["id"] = jObject.optString("id")
                hash["name"] = jObject.optString("name")

               val district = District(jObject.optString("id"), jObject.optString("name"))
                districtViewModel.insert(district)

            }


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun sendAttendance(id: String, rfid: String, regId: String, distId: String) = runBlocking {

        val api = API()
        val body = mapOf(
            "rfid_no" to rfid,
            "region_id" to regId,
            "district_id" to distId
        )

        try {
            GlobalScope.launch {
                val res:String = api.postAPIWithHeader(
                    Constant.URL + "api/attendance",
                    body,
                    this@MainBase
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){
                        // Toast.makeText(this@Startpage, "$rfid failed to create attendance", Toast.LENGTH_SHORT).show()



                    }else {
                        setInfo(res, id, rfid,regId, distId)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this@MainBase, "Error: Failed to create supervisor", Toast.LENGTH_SHORT).show()



        }

    }

    private fun setInfo(res: String, id: String, rfid: String, regId: String, distId: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

           // Toast.makeText(this@MainBase, mess, Toast.LENGTH_SHORT).show()
            attendanceViewModel.deleteByRfid(rfid)
            sendViewModel.updateSend(id)
            if(mess == "Attendance created successfully"){
                println("morking")
            }
        }catch (e: Exception){
            e.printStackTrace()

        }
    }

}