package com.dawolf.yea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.database.login.LoginViewModel
import com.dawolf.yea.databinding.FragmentUserUpdateBinding
import com.dawolf.yea.fragments.StartPage
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
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
 * Use the [UserUpdate.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserUpdate : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var storage: Storage
    private lateinit var binding: FragmentUserUpdateBinding

    private var arrayListRegion = ArrayList<HashMap<String, String>>()
    private var arrayListDistrict = ArrayList<HashMap<String, String>>()
    private lateinit var loginViewModel: LoginViewModel


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
        val view = inflater.inflate(R.layout.fragment_user_update, container, false)
        binding = FragmentUserUpdateBinding.bind(view)
        storage = Storage(requireContext())
        loginViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[LoginViewModel::class.java]


        getButtons()

        return view
    }

    private fun getButtons() {
        if(storage.regionId !="null"){
            binding.spinRegion.visibility = View.GONE
        }

        if(storage.districtId !="null"){
            binding.spinDistrict.visibility = View.GONE
        }

        binding.btnSubmit.setOnClickListener {

                ShortCut_To.hideKeyboard(requireActivity())

            if(binding.edtPassword.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter the user password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtConfirm.text.toString()!=binding.edtPassword.text.toString()){
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(storage.regionId == "" || storage.regionId =="null"){
                binding.spinRegion.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Select your region", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(storage.districtId == "" || storage.districtId == "null"){
                binding.spinDistrict.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Select your district", Toast.LENGTH_SHORT).show()
                requireActivity()
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
                        OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                            if(position == 0){
                                storage.regionId = "null"
                                binding.spinDistrict.setSelection(0)
                                binding.spinDistrict.visibility = View.GONE
                            }else{
                                storage.regionId = arrayListRegion[position-1]["id"]
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
//                getRegion()
//                getDistrict()
            }
        }

    }
    fun getDistrctFromRegion(districts: String){
        try {
            val jsonArray = JSONArray(districts)
            val list = ArrayList<String>()
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

                binding.spinDistrict.onItemSelectedListener = object : OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if(p2==0){
                            storage.districtId = "null"
                        }else{
                            storage.districtId = arrayListDistrict[p2-1]["id"]
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

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
        "name" to storage.uSERNAME!!,
        "phone" to storage.phone!!,
        "email" to storage.email!!,
        "password" to binding.edtPassword.text.toString(),
        "password_confirmation" to binding.edtConfirm.text.toString(),
        "district_id" to storage.districtId!!,
        "region_id" to storage.regionId!!
    )

    try {
        GlobalScope.launch {
            val res:String = api.postAPIWithHeader(
                Constant.URL + "api/register/update/${storage.uSERID}",
                body,
                requireActivity()
            )

            withContext(Dispatchers.Main){
                if(res == "[]"){
                    Toast.makeText(requireContext(), "Error: Failed to create new user", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true


                }else {
                    setInfo(res)
                }
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
        Toast.makeText(requireContext(), "Error: Failed to create new user", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
        binding.btnSubmit.isEnabled = true


    }

}


    private fun setInfo(res: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

            Toast.makeText(requireContext(), mess, Toast.LENGTH_SHORT).show()
            if (mess == "User updated successfully") {
                loginViewModel.updateConfirm(binding.edtConfirm.text.toString(), storage.regionId!!, storage.districtId!!, ShortCut_To.currentDateFormat2, storage.uSERID!!)
                (activity as MainBase).navTo(StartPage(), "Welcome", "confirm", 0)
            }
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Error: Failed to create new user. Please try again",
                Toast.LENGTH_SHORT
            ).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserUpdate.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserUpdate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}