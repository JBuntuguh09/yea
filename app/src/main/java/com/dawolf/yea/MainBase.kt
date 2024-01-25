package com.dawolf.yea

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.database.district.District
import com.dawolf.yea.database.district.DistrictViewModel
import com.dawolf.yea.database.login.Login
import com.dawolf.yea.database.region.Region
import com.dawolf.yea.database.region.RegionViewModel
import com.dawolf.yea.database.send.SendViewModel
import com.dawolf.yea.database.signout.SignoutViewModel
import com.dawolf.yea.databinding.ActivityMainBaseBinding
import com.dawolf.yea.dialogue.ShowMe
import com.dawolf.yea.fragments.StaffBase
import com.dawolf.yea.fragments.StartPage
import com.dawolf.yea.fragments.attendance.ViewAttendance
import com.dawolf.yea.fragments.signout.Signout
import com.dawolf.yea.fragments.user.ViewUsers
import com.dawolf.yea.resources.Constant
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
import java.io.IOException

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

    private val mAdapter: ArrayAdapter<String>? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    var toolbar: Toolbar? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    var idCheck = MutableLiveData<String>()
    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    var lat = "0.00"
    var long="0.00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_base)
        binding = ActivityMainBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Storage(this)
        loginTo()
        regionViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[RegionViewModel::class.java]
        districtViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[DistrictViewModel::class.java]
        sendViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SendViewModel::class.java]
        attendanceViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[AttendanceViewModel::class.java]
        signoutViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SignoutViewModel::class.java]
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        checkLocationPermission()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {

        } else {
            pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )
        }


        getRegion()
        getDistrict()
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
            navTo(StaffBase(), "Staff", "Start", 1)

        }

        binding.cardAttendance.setOnClickListener {
            binding.drawLay.closeDrawer(GravityCompat.START)
            navTo(ViewAttendance(), "Attendance", "Start", 1)
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
                    println("here we go  "+ data)
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
            println("bbbbbbbbbb"+res)
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

            // Toast.makeText(this@MainBase, mess, Toast.LENGTH_SHORT).show()
            signoutViewModel.deleteByRfidId(rfid, storage.uSERID!!)
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
    private fun getDistrict(){

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
        lat: String,
        long: String
    ) = runBlocking {

        val api = API()
        val body = mapOf(
            "rfid_no" to rfid,
            "region_id" to regId,
            "district_id" to distId,
            "latitude" to lat,
            "longitude" to long
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
            attendanceViewModel.deleteAllByRfidId(rfid, storage.uSERID!!)
            sendViewModel.updateSend(id)
            if(mess == "Attendance created successfully"){
                println("morking")
            }
        }catch (e: Exception){
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

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            // Extract information from the intent's extras if needed
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

            // Process the tag data as needed
            // You might want to use NFC-related classes like Ndef or IsoDep to handle specific tag types

            // For example, you can read the UID of the tag:
            val uidBytes: ByteArray? = tag?.id

            val uid = uidBytes?.joinToString(":") { byte -> String.format("%02X", byte) }
            val uidString: String = byteArrayToHexString(uidBytes!!)

            // Now 'uidString' contains the UID of the NFC tag
            // Print or use the UID as needed
            println("NFC Tag UID: $uidString")


            // Now 'uid' contains the UID of the NFC tag
            // Print the UID as a string
            println("NFC Tag UID: $uid")




            if (tag != null) {
                val mifareClassic = MifareClassic.get(tag)

                try {
                    mifareClassic?.connect()

                    if (mifareClassic != null && mifareClassic.isConnected) {
                        // Authenticate with the MIFARE Classic card
                        val sector = 0 // Sector 0
                        mifareClassic.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT)

                        // Read the content of block 0 in sector 0
                        val blockNumber = sector * 4 // Each sector has 4 blocks
                        val blockData = mifareClassic.readBlock(blockNumber)

                        // Convert blockData to a readable format
                        val dataAsString = String(blockData, Charsets.UTF_8)

                        println("Data from Sector $sector, Block $blockNumber: $dataAsString")
                        val hexString = blockData.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

                        println("Hexadecimal Data from Sector $sector, Block $blockNumber: $hexString")
                        updateLiveData(hexString)
                        ShowMe.ScanOptions(this, binding.linList, hexString, attendanceViewModel, signoutViewModel)

                    } else {
                        println("MifareClassic is null or not connected.")
                    }
                } catch (e: IOException) {
                    println("Error during MifareClassic communication: ${e.message}")
                } finally {
                    try {
                        mifareClassic?.close()
                    } catch (e: IOException) {
                        println("Error closing MifareClassic: ${e.message}")
                    }
                }
            }

            // Handle the UID or any other data you're interested in
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

            if (tag != null) {
                val isoDep = IsoDep.get(tag)

                try {
                    isoDep?.connect()

                    if (isoDep != null && isoDep.isConnected) {
                        // Authenticate with the MIFARE Classic card
                        val key = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
                        val sector = 0 // Sector 0
                        isoDep.transceive(byteArrayOf(0x1A.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
                        isoDep.transceive(byteArrayOf(0x0A.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), sector.toByte()))

                        // Read the content of block 0 in sector 0
                        val blockNumber = sector * 4 // Each sector has 4 blocks
                        val readCommand = byteArrayOf(0x30.toByte(), blockNumber.toByte())
                        val blockData = isoDep.transceive(readCommand)

                        // Convert blockData to a readable format
                        val dataAsString = String(blockData, Charsets.UTF_8)

                        println("Data from Sector $sector, Block $blockNumber: $dataAsString")
                    } else {
                        println("IsoDep is null or not connected.")
                    }
                } catch (e: IOException) {
                    println("Error during IsoDep communication: ${e.message}")
                } finally {
                    try {
                        isoDep?.close()
                    } catch (e: IOException) {
                        println("Error closing IsoDep: ${e.message}")
                    }
                }
            }
        }
    }

    private fun byteArrayToHexString(array: ByteArray): String {
        val hexChars = CharArray(array.size * 2)
        for (i in array.indices) {
            val v = array[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }

        return String(hexChars)
    }

    private fun checkLocationPermission() {
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



            }else{

               // Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
                var mess = jsonObject.optString("message", "")
                if(mess.isEmpty()){
                    mess = jsonObject.optString("errors", "Error. Please check your credentials")
                }


            }
        }catch (e:Exception){
            println("============"+res)
          //  Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

}