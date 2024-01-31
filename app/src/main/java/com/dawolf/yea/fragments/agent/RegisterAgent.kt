package com.dawolf.yea.fragments.agent

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.RFIDActivity2
import com.dawolf.yea.database.agent.Agent
import com.dawolf.yea.database.agent.AgentViewModel
import com.dawolf.yea.database.supervisor.Supervisor
import com.dawolf.yea.databinding.FragmentRegisterAgentBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.MyLocationListener
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterAgent.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterAgent : Fragment(), LocationListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterAgentBinding
    private lateinit var storage: Storage
    private var arrayListRegion = ArrayList<HashMap<String, String>>()
    private var arrayListDistrict = ArrayList<HashMap<String, String>>()
    private var arrayListSup = ArrayList<HashMap<String, String>>()
    var rfid =""
    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    var lat = "1.00"
    var long="1.00"
    private lateinit var agentViewModel: AgentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register_agent, container, false)
        binding = FragmentRegisterAgentBinding.bind(view)
        storage = Storage(requireContext())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        agentViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AgentViewModel::class.java]

        getButtons()
        (activity as MainBase).getSupervisors()
        checkLocationPermission()
        return view
    }

    private fun getButtons() {
        (activity as MainBase).supervisorsCheck.observe(requireActivity()){data->
            if(data!=null){
                val jsonObject = JSONObject(data)
                val jdata = jsonObject.getJSONArray("data")
                val list = ArrayList<String>()
                list.add("Select Team Leader")
               arrayListSup.clear()
                for(a in 0 until jdata.length()){
                    val jObject = jdata.getJSONObject(a)
                    val hash = HashMap<String, String>()

                    hash["id"] = jObject.optString("id")
                    hash["supervisor_id"] = jObject.optString("supervisor_id")
                    hash["name"] = jObject.optString("name")
                    hash["status"] = jObject.optString("status")

                    if (hash["status"]!!.lowercase() == "active") {
                        arrayListSup.add(hash)
                        list.add(hash["name"]!!)
                    }
                }
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
                arrayAdapter.setDropDownViewResource(R.layout.layout_dropdown)
                binding.spinSupervisor.adapter = arrayAdapter
                binding.spinSupervisor.visibility = View.VISIBLE
            }


        }

        binding.btnScan.setOnClickListener {
            storage.project = "Register"
            val intent = Intent(requireContext(), RFIDActivity2::class.java)
            startActivity(intent)
        }
        val list = ArrayList<String>()
        list.add("Select Gender")
        list.add("Female")
        list.add("Male")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
        arrayAdapter.setDropDownViewResource(R.layout.layout_dropdown)
        binding.spinGender.adapter = arrayAdapter

        binding.edtDob.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtDob.right - binding.edtDob.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    // your action here
                    ShortCut_To.showCal(binding.edtDob, requireContext())
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.btnSubmit.setOnClickListener {
            ShortCut_To.hideKeyboard(requireActivity())
            if(rfid==""){
                Toast.makeText(requireContext(), "Scan your Beneficiary card", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.edtName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter agent name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.edtName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter Beneficiary date of birth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.spinGender.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select Beneficiary gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtPhone.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter Beneficiary phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.spinRegion.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select Beneficiary region", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.spinDistrict.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select Beneficiary district", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requireActivity().runOnUiThread {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSubmit.isEnabled = false
            }
            sendData()
        }
        (activity as MainBase).regionViewModel.liveData.observe(requireActivity()){ data->
            if(data.isNotEmpty()){

                val lists = ArrayList<String>()
                lists.add("Select region")
                for(a in data.indices){
                    val jObject = data[a]
                    val hash = HashMap<String, String>()
                    hash["id"] = jObject.id
                    hash["name"] = jObject.name
                    hash["districts"] =jObject.districts

                    arrayListRegion.add(hash)
                    lists.add(jObject.name)

                }

                if(arrayListRegion.size>0){
                    val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, lists)
                    adapter.setDropDownViewResource(R.layout.layout_dropdown)
                    binding.spinRegion.adapter = adapter

                    binding.spinRegion.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                            if(position == 0){
                                binding.spinDistrict.setSelection(0)
                                binding.spinDistrict.visibility = View.GONE
                            }else{
                                binding.spinDistrict.visibility = View.VISIBLE
                                getDistrctFromRegion(data[position-1].districts)

                            }
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                            // Handle case where nothing is selected (optional)
                        }
                    }
                }
            }else{
                getRegion()
                getDistrict()
            }
        }

    }
    fun getDistrctFromRegion(districts: String){
        try {
            val jsonArray = JSONArray(districts)
            val list = ArrayList<String>()
            arrayListDistrict.clear()
            list.add("Select district")
            for (a in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(a)
                val hash = HashMap<String, String>()
                hash["id"] = jsonObject.optString("id")
                hash["name"] = jsonObject.optString("name")

                arrayListDistrict.add(hash)
                list.add(jsonObject.optString("name"))

            }

            if(arrayListDistrict.size>0){
                val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
                adapter.setDropDownViewResource(R.layout.layout_dropdown)
                binding.spinDistrict.adapter = adapter
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendData() = runBlocking {
        val myLocationListener = MyLocationListener(requireContext())

        val api = API()
        val body = mapOf(
            "rfid_no" to  rfid,
            "name" to binding.edtName.text.toString(),
            "phone" to binding.edtPhone.text.toString(),
            "gender" to binding.spinGender.selectedItem.toString(),
            "dob" to ShortCut_To.reverseDate(binding.edtDob.text.toString(), "/",  "-"),
            "region_id" to arrayListRegion[binding.spinRegion.selectedItemPosition-1]["id"]!!,
            "district_id" to arrayListDistrict[binding.spinDistrict.selectedItemPosition-1]["id"]!!,
            "address" to binding.edtAddress.text.toString(),
            "latitude" to lat,
            "longitude" to long
        )

        println("Akuu $body")
        try {
            GlobalScope.launch {
                val res:String = api.postAPIWithHeader(
                    Constant.URL + "api/agent",
                    body,
                    requireActivity()
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){
                        Toast.makeText(requireContext(), "Error: Failed to create Beneficiary", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.isEnabled = true


                    }else {
                        setInfo(res)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: Failed to create agent", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true


        }

    }

    private fun setInfo(res: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")


            Toast.makeText(requireContext(), mess, Toast.LENGTH_SHORT).show()
            if(mess == "Agent created successfully"){
                try {
                    val data = jsonObject.getJSONObject("data")
                    val agent = Agent(rfid, storage.uSERID!!, data.optString("id"), data.optString("agent_id"), data.optString("name"), data.optString("dob"),
                        data.optString("phone"), data.optString("address"), binding.spinRegion.selectedItem.toString(), data.optString("region_id"),
                        binding.spinDistrict.selectedItem.toString(), data.optString("district_id"), long, lat, "", "", "",
                        "", "Active", data.optString("created_at"), data.optString("updated_at"), data.optString("gender"),
                        )

                    agentViewModel.insert(agent)
                }catch (_: Exception){

                }
                requireActivity().onBackPressed()
            }
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }catch (e: Exception){
            e.printStackTrace()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getRegion(){

        binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/regions",  requireActivity())
                withContext(Dispatchers.Main){

                    setRegionInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show()

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
            list.add("Select Beneficiary region")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()
                hash["id"] = jObject.optString("id")
                hash["name"] = jObject.optString("name")

                arrayListRegion.add(hash)
                list.add(jObject.optString("name"))

            }

            if(arrayListRegion.size>0){

                val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
                adapter.setDropDownViewResource(R.layout.layout_dropdown)
                binding.spinRegion.adapter = adapter
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
                val res = api.getAPI(Constant.URL+"api/districts",  requireActivity())
                withContext(Dispatchers.Main){

                    setDistrictInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show()

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
            list.add("Select Beneficiary districts")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()
                hash["id"] = jObject.optString("id")
                hash["name"] = jObject.optString("name")

                arrayListDistrict.add(hash)
                list.add(jObject.optString("name"))

            }

            if(arrayListRegion.size>0){

                val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
                adapter.setDropDownViewResource(R.layout.layout_dropdown)
                binding.spinDistrict.adapter = adapter
            }
        }catch (e:Exception){
            Toast.makeText(requireContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if(storage.randVal!!.isEmpty()){
            binding.btnScan.text= "Scan Card"
            rfid =""
        }else {
            binding.btnScan.text = storage.randVal
            rfid = storage.randVal!!
        }
       // checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L, 0f, this
        )
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
        lat = latitude.toString()
        long = longitude.toString()

        // Use latitude and longitude as needed
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterAgent.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterAgent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}