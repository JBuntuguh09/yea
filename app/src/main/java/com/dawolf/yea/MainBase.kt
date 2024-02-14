package com.dawolf.yea

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.database.Attendances.Attendances
import com.dawolf.yea.database.Attendances.AttendancesViewModel
import com.dawolf.yea.database.agent.Agent
import com.dawolf.yea.database.agent.AgentViewModel
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictViewModel
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionViewModel
import com.dawolf.yea.database.send.SendViewModel
import com.dawolf.yea.database.signout.SignoutViewModel
import com.dawolf.yea.database.supervisor.Supervisor
import com.dawolf.yea.database.supervisor.SupervisorViewModel
import com.dawolf.yea.databinding.ActivityMainBaseBinding
import com.dawolf.yea.fragments.StaffBase
import com.dawolf.yea.fragments.StartPage
import com.dawolf.yea.fragments.attendance.ViewAttendance
import com.dawolf.yea.fragments.signout.Signout
import com.dawolf.yea.fragments.user.ViewUsers
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainBase : AppCompatActivity(), LocationListener {
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

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    var toolbar: Toolbar? = null
    private var mToggle: ActionBarDrawerToggle? = null
    var idCheck = MutableLiveData<String>()
    var supervisorsCheck = MutableLiveData<String>()
    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    var lat = "0.00"
    var long="0.00"
    private lateinit var agentViewModel: AgentViewModel
    private lateinit var supervisorViewModel: SupervisorViewModel
    private lateinit var attendancesViewModel: AttendancesViewModel
     var communites = MutableLiveData<String>()
     var areas = MutableLiveData<String>()

    var noAttend = MutableLiveData<String>()
    var noAgent = MutableLiveData<String>()
    var noSuper = MutableLiveData<String>()


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

        agentViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[AgentViewModel::class.java]
        supervisorViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SupervisorViewModel::class.java]
        attendancesViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[AttendancesViewModel::class.java]
        loginTo()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        checkLocationPermission()


        getSupervisors()
        getRegion()
        getDistrict()
        getCommunity()
        getArea()

//        if (storage.first1 == 1){
//            navTo(UserUpdate(), "Update Profile", "Login", 0)
//        }else{
//            navTo(StartPage(), "Welcome", "Login", 0)
//        }
        navTo(StartPage(), "Welcome", "Login", 0)


        getButtons()
        getWatchers()

    }

    fun updateLiveData(newValue: String) {
        idCheck.value = newValue
    }

    fun updateSupLiveData(newValue: String) {
        supervisorsCheck.value = newValue
    }

    fun getButtons(){
        toolbar = findViewById(R.id.tabSettings)
        navigationView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawLay)
        navigationView.setNavigationItemSelectedListener { false }
        mToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        //mToggle!!.setHomeAsUpIndicator(R.drawable.pencil_outline)
        mToggle!!.drawerArrowDrawable.color = Color.BLACK


        binding.txtName.text = storage.uSERNAME
        binding.txtPhone.text = storage.phone
        binding.txtEmail.text = storage.email

        binding.cardStaff.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
            navTo(StaffBase(), "Registration", "Start", 1)

        }

        binding.cardAttendance.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
            navTo(ViewAttendance(), "Sign In", "Start", 1)
        }

        binding.cardUsers.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
           navTo(ViewUsers(), "Users", "Start", 1)
        }

        binding.cardSignOut.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
           navTo(Signout(), "Sign Out", "Start", 1)
        }

        binding.cardLogoutOut.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
            finish()
        }

    }

    fun getWatchers(){

        sendViewModel.liveDataAttend.observe(this){data->
            if(data.isNotEmpty()){
                try {

                    for (a in data.indices){
                        sendAttendance(data[a].id.toString(), data[a].rfid, data[a].region_id, data[a].district_id, data[a].lat, data[a].longi)
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

        sendViewModel.liveDataSignout.observe(this){data->
            if(data.isNotEmpty()){
                try {

                    for (a in data.indices){
                        senDSign(data[a].id.toString(), data[a].rfid, data[a].signout_date, data[a].lat, data[a].longi)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

        sendViewModel.liveData.observe(this){data->
            if(data.isNotEmpty()){
                val arrList = ArrayList<HashMap<String, String>>()

            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun senDSign(id: String, rfid: String, signoutDate: String, lats: String,
                         longs: String) {
        val api = API()
        val body = mapOf(
            "signout_date" to signoutDate,
            "signout_latitude" to lats,
            "signout_longitude" to longs

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
            signoutViewModel.deleteByRfidId(rfid, storage.uSERID!!)
            sendViewModel.updateSend(id)
            Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
            println(mess+"//mooooo")

        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Failed to signed out  $rfid", Toast.LENGTH_SHORT).show()

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
     fun getRegion(){

        //binding.progressBar.visibility = View.VISIBLE
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
     fun getDistrict(){

       // binding.progressBar.visibility = View.VISIBLE
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

    private fun sendAttendance(
        id: String,
        rfid: String,
        regId: String,
        distId: String,
        lats: String,
        longs: String
    ) = runBlocking {

        val api = API()
        val body = mapOf(
            "rfid_no" to rfid,
            "region_id" to regId,
            "district_id" to distId,
            "signin_latitude" to lats,
            "signin_longitude" to longs
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
            attendanceViewModel.deleteAllByRfidId(rfid, storage.uSERID!!)
            sendViewModel.updateSend(id)
            Toast.makeText(this@MainBase, "Error: Failed to create sign in", Toast.LENGTH_SHORT).show()



        }

    }

    private fun setInfo(res: String, id: String, rfid: String, regId: String, distId: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

           // Toast.makeText(this@MainBase, mess, Toast.LENGTH_SHORT).show()
            attendanceViewModel.deleteAllByRfidId(rfid, storage.uSERID!!)
            sendViewModel.updateSend(id)

            Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
//            if(mess == "Attendance created successfully"){
//
//
//            }else{
//
////                sendViewModel.updateSend(id)
//            }
        }catch (e: Exception){
            attendanceViewModel.deleteAllByRfidId(rfid, storage.uSERID!!)
            sendViewModel.updateSend(id)
            Toast.makeText(this, "Failed to signed in for $rfid", Toast.LENGTH_SHORT).show()
            e.printStackTrace()

        }
    }

    override fun onBackPressed() {
        try {
            binding.imgEdit.visibility = View.GONE
        }catch (e : Exception){
            e.printStackTrace()
        }
        super.onBackPressed()
    }



    private fun checkLocationPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                requestLocationUpdates()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L, 0f, this
            )
        } catch (ex: SecurityException) {
            Log.e("Location", "Permission not granted", ex)
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        lat = location.latitude.toString()
        long = location.longitude.toString()

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle status changes if needed
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled if needed
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled if needed
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()
                } else {
                    // Handle permission denied
                }
            }
        }
    }


    private fun loginTo() =runBlocking {
        val api = API()

        val formdata = mapOf(
            "username" to storage.email!!,
            "password" to storage.pASSWORD!!)


        try {
            GlobalScope.launch {
                val res:String = api.postAPI(
                    Constant.URL + "api/login",
                    formdata,
                    this@MainBase,
                    binding.constLogout
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){

                    }else {
                        setInfo(res)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()



        }

    }

    private fun setInfo(res: String) {

        try {
            val jsonObject = JSONObject(res)
            if(jsonObject.optString("message").equals("User logged in successfully")){

                val data = jsonObject.getJSONObject("data")
                val token = jsonObject.getJSONObject("meta")["token"].toString()


                val userId = data["id"].toString()
                val username  = data["name"].toString()
                val email  = data["email"].toString()
                val phone  = data["phone"].toString()

                val region_id  = data["region_id"].toString()
                val district_id  = data["district_id"].toString()
                val changed_password_at  = data["changed_password_at"].toString()
                val email_verified_at  = data["email_verified_at"].toString()


                storage.regionId = region_id
                storage.districtId = district_id
                storage.tokenId = "Bearer $token"
                storage.token = token
                storage.uSERID = userId
                storage.uSERNAME = username
                storage.email = email
                storage.phone = phone
                //storage.pASSWORD = binding.edtPassword.text.toString()
                //println("UsererIentifier $userId")

                getAgents()
                getMySupervisors()
                getAttendance()

            }else{

               // Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
                var mess = jsonObject.optString("message", "")
                if(mess.isEmpty()){
                    mess = jsonObject.optString("errors", "Error. Please check your credentials")
                }


            }
        }catch (e:Exception){

          //  Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getSupervisors() {

        //progress.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/supervisors",  this@MainBase)
                withContext(Dispatchers.Main){

                    setSuperInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    //Toast.makeText(this@MainBase, "No data found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setSuperInfo(res: String) {
        binding.progressBar.visibility = View.GONE
        try {

            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            updateSupLiveData(res)
            storage.projSupervisors = res


        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun showSending(){
        val layoutInflater = LayoutInflater.from(this)
        val view  = layoutInflater.inflate(R.layout.layout_pop_send, binding.linMain, false)

    }

    ////////////////////
    @OptIn(DelicateCoroutinesApi::class)
    private fun getAgents(){
        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/agent/user/${storage.uSERID!!}",  this@MainBase)
                withContext(Dispatchers.Main){

                    setAgentInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){


                }
            }
        }
    }

    private fun setAgentInfo(res: String) {
        try {
            println(res)
            var num = 0
            val jsonObject = JSONObject(res)
            try {
                val mess = jsonObject.optString("message")
                if (mess == "Team Leader not found" || mess == "Beneficiary not found"){
                    agentViewModel.deleteAgent(storage.uSERID!!)
                    noAgent.value = "Yes"
                }
            }catch (e: Exception){

            }
            val data = jsonObject.getJSONArray("data")
             agentViewModel.deleteAgent(storage.uSERID!!)

                for (a in 0 until data.length()) {
                    val jObject = data.getJSONObject(a)
                    if (jObject.getString("status") == "Active") {
                        num += 1
                    }
                    val agent = Agent(
                        jObject.getString("rfid_no"),
                        storage.uSERID!!,
                        jObject.getString("id"),
                        jObject.getString("agent_id"),
                        jObject.getString("name"),
                        jObject.getString("dob"),
                        jObject.getString("phone"),
                        jObject.getString("address"),
                        jObject.getJSONObject("region").getString("name"),
                        jObject.getString("region_id"),
                        jObject.getJSONObject("district").getString("name"),
                        jObject.getString("district_id"),
                        jObject.getString("rfid_no"),
                        jObject.getString("longitude"),
                        jObject.getJSONObject("supervisor").getString("name"),
                        jObject.getString("supervisor_id"),
                        jObject.getJSONObject("supervisor").getString("email"),
                        jObject.getJSONObject("supervisor").getString("phone"),
                        jObject.getString("status"),
                        jObject.getString("created_at"),
                        jObject.getString("updated_at"),
                        jObject.getString("gender")
                    )

                    agentViewModel.insert(agent)

            }

            noAgent.value = "No"
        }catch (e: Exception){

            e.printStackTrace()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getMySupervisors(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/supervisor/user/${storage.uSERID!!}",  this@MainBase)
                withContext(Dispatchers.Main){

                    setMySuperInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){


                }
            }
        }
    }

    private fun setMySuperInfo(res: String) {
        try {
            var num = 0
            val jsonObject = JSONObject(res)
            try {
                val mess = jsonObject.optString("message")
                if (mess == "Team leader not found"){
                    supervisorViewModel.deleteSuper(storage.uSERID!!)
                    noSuper.value = "Yes"
                }
                //Attendance not found
            }catch (_: Exception){

            }
            val data = jsonObject.getJSONArray("data")

            supervisorViewModel.deleteSuper(storage.uSERID!!)
                for (a in 0 until data.length()) {
                    val jObject = data.getJSONObject(a)
                    if (jObject.getString("status") == "Active") {
                        num += 1
                    }
                    val supervisor = Supervisor(
                        jObject.getString("supervisor_id"),
                        storage.uSERID!!,
                        jObject.getString("id"),
                        jObject.getString("name"),
                        jObject.getString("phone"),
                        jObject.getString("status"),
                        jObject.getJSONObject("region").getString("name"),
                        jObject.getString("region_id"),
                        jObject.getJSONObject("district").getString("name"),
                        jObject.getString("district_id"),
                        jObject.getString("created_at")
                    )

                    supervisorViewModel.insert(supervisor)
            }

            noSuper.value = "No"
        }catch (e: Exception){

            e.printStackTrace()
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getAttendance(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/attendance/user/${storage.uSERID!!}",  this@MainBase)
                withContext(Dispatchers.Main){

                    setAttendInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    // Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setAttendInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE

            var day = 0
            var week = 0
            var month = 0
            val jsonObject = JSONObject(res)
            try {
                val mess = jsonObject.optString("message")
                if (mess == "Attendance not found"){
                    attendancesViewModel.deleteBid(storage.uSERID!!)
                    noAttend.value = "Yes"
                }
                //
            }catch (_: Exception){

            }
            val data = jsonObject.getJSONArray("data")
            attendancesViewModel.deleteBid(storage.uSERID!!)
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "day")){
                    day += 1
                }

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "week")){
                    week += 1
                }

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "month")){
                    month += 1
                }


                val attendances = Attendances(jObject.getString("id"), storage.uSERID!!,jObject.getString("rfid_no"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id"),
                    jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"),
                    jObject.getJSONObject("supervisor").getString("name"), jObject.getJSONObject("supervisor").getString("id"),
                    jObject.getJSONObject("agent").getString("name"),  jObject.getJSONObject("agent").getString("id"),
                    jObject.getString("signout_date"), jObject.getString("signout_by"), jObject.getString("created_at"))
                attendancesViewModel.insert(attendances)


            }

            noAttend.value = "No"

        }catch (e: Exception){

            e.printStackTrace()
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
     fun getCommunity(){

        // binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/communities",  this@MainBase)
                withContext(Dispatchers.Main){
                  //  setCommunityInfo(res)
                    communites.value = res
                    storage.projCommunities = res
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

    private fun setCommunityInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            val list = ArrayList<String>()
            list.add("Select communities")
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

    ////////////////////
    @OptIn(DelicateCoroutinesApi::class)
     fun getArea(){

        // binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/deployment-areas",  this@MainBase)
                withContext(Dispatchers.Main){
                    //setAreaInfo(res)
                    areas.value = res
                    storage.projAreas = res
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

    private fun setAreaInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            val list = ArrayList<String>()
            list.add("Select deployment areas")
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

}