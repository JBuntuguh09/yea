package com.dawolf.yea.fragments.supervisor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.database.supervisor.SupervisorViewModel
import com.dawolf.yea.databinding.FragmentEditSupervisorBinding
import com.dawolf.yea.resources.Constant
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
 * Use the [EditSupervisor.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditSupervisor : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentEditSupervisorBinding
    private lateinit var storage: Storage
    private var arrayListRegion = ArrayList<HashMap<String, String>>()
    private var arrayListDistrict = ArrayList<HashMap<String, String>>()
    private lateinit var supervisorViewModel: SupervisorViewModel
    var id = ""
    var ids = ""
    var regId = ""
    var distId = ""

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
        val view = inflater.inflate(R.layout.fragment_edit_supervisor, container, false)
        binding = FragmentEditSupervisorBinding.bind(view)
        storage = Storage(requireContext())
        supervisorViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[SupervisorViewModel::class.java]
         id = (activity as MainBase).arrayListSuper["supervisor_id"]!!
         ids = (activity as MainBase).arrayListSuper["id"]!!

        getButtons()
        getWatchers()
        return view
    }

    private fun getWatchers() {

        supervisorViewModel.getSuper(id).observe(requireActivity()) { data ->
            if (data.isNotEmpty()) {
                val hash = data[0]
                binding.edtName.setText(hash.name)
                binding.edtPhone.setText(hash.phone)
                regId = hash.region_id
                distId = hash.district_id
                if (arrayListRegion.size > 0) {
                    println("ggg2 $regId")
                    for (a in arrayListRegion.indices) {

                        if (arrayListRegion[a]["id"] == hash.region_id) {
                            binding.spinRegion.setSelection(a + 1)
                            for(b in arrayListDistrict.indices){
                                if(arrayListDistrict[b]["id"]==hash.district_id){
                                    binding.spinDistrict.setSelection(a+1)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun getButtons() {
        binding.btnSubmit.setOnClickListener {
            ShortCut_To.hideKeyboard(requireActivity())
            if(binding.edtName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter supervisor name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtPhone.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter supervisor phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.spinRegion.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select supervisor region", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.spinDistrict.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select supervisor district", Toast.LENGTH_SHORT).show()
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

                val list = ArrayList<String>()
                list.add("Select region")
                for(a in data.indices){
                    val jObject = data[a]
                    val hash = HashMap<String, String>()
                    hash["id"] = jObject.id
                    hash["name"] = jObject.name
                    hash["districts"] =jObject.districts

                    arrayListRegion.add(hash)
                    list.add(jObject.name)

                }

                if(arrayListRegion.size>0){
                    val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
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

                    if (arrayListRegion.size > 0) {

                        for (a in arrayListRegion.indices) {
                            if (arrayListRegion[a]["id"] == regId) {
                                binding.spinRegion.setSelection(a + 1)

                                for(b in arrayListDistrict.indices){
                                    if(arrayListDistrict[b]["id"]==distId){
                                        binding.spinDistrict.setSelection(a+1)
                                    }
                                }

                            }
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

                for(b in arrayListDistrict.indices){
                    if(arrayListDistrict[b]["id"]==distId){
                        binding.spinDistrict.setSelection(b+1)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun sendData() = runBlocking {
        val api = API()
        val body = mapOf(
            "name" to binding.edtName.text.toString(),
            "phone" to binding.edtPhone.text.toString(),
            "region_id" to arrayListRegion[binding.spinRegion.selectedItemPosition-1]["id"]!!,
            "district_id" to arrayListDistrict[binding.spinDistrict.selectedItemPosition-1]["id"]!!
        )

        try {
            GlobalScope.launch {
                val res:String = api.postAPIWithHeader(
                    Constant.URL + "api/supervisor/update/$ids",
                    body,
                    requireActivity()
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){
                        Toast.makeText(requireContext(), "Error: Failed to create supervisor", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.isEnabled = true


                    }else {
                        setInfo(res)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: Failed to create supervisor. Please try again", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true


        }

    }

    private fun setInfo(res: String) {
        try {

            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")



            Toast.makeText(requireContext(), mess, Toast.LENGTH_SHORT).show()
            if(mess == "Supervisor updated successfully"){
                try {
                    supervisorViewModel.updateSupervisor(binding.edtName.text.toString(), binding.edtPhone.text.toString(),
                        binding.spinRegion.selectedItem.toString(), arrayListRegion[binding.spinRegion.selectedItemPosition-1]["id"]!!,
                        binding.spinDistrict.selectedItem.toString(), arrayListDistrict[binding.spinDistrict.selectedItemPosition-1]["id"]!!,id)
                }catch (_: Exception){

                }
                requireActivity().onBackPressed()
            }
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: Failed to create supervisor. Please try again", Toast.LENGTH_SHORT).show()
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
            list.add("Select supervisor region")
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
            list.add("Select supervisor districts")
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
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditSupervisor.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditSupervisor().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}