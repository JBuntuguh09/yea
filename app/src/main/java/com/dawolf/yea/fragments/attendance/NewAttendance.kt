package com.dawolf.yea.fragments.attendance

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.Startpage
import com.dawolf.yea.databinding.FragmentNewAttendanceBinding
import com.dawolf.yea.resources.Storage
import com.rscja.deviceapi.RFIDWithUHFA4NetWork
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewAttendance.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewAttendance : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNewAttendanceBinding
    private var arrayListRegion = ArrayList<HashMap<String, String>>()
    private var arrayListDistrict = ArrayList<HashMap<String, String>>()
    private lateinit var storage: Storage

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
        val view = inflater.inflate(R.layout.fragment_new_attendance, container, false)
        binding = FragmentNewAttendanceBinding.bind(view)
        storage= Storage(requireContext())


        getButtons()
        return view
    }

    private fun getButtons() {
        binding.btnSubmit.setOnClickListener {
            if(binding.spinRegion.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select a region", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.spinDistrict.selectedItemPosition==0){
                Toast.makeText(requireContext(), "Select a district", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            storage.region = binding.spinRegion.selectedItem.toString()
            storage.district = binding.spinDistrict.selectedItem.toString()
            storage.regionId = arrayListRegion[binding.spinRegion.selectedItemPosition-1]["id"]
            storage.districtId = arrayListDistrict[binding.spinDistrict.selectedItemPosition-1]["id"]

            val intent = Intent(requireContext(), Startpage::class.java)
            startActivity(intent)



        }
        (activity as MainBase).regionViewModel.liveData.observe(requireActivity()){data->
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

                    binding.spinRegion.onItemSelectedListener = object : OnItemSelectedListener {
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
            }
        }

//        (activity as MainBase).districtViewModel.liveData.observe(requireActivity()){data->
//            if(data.isNotEmpty()){
//                val list = ArrayList<String>()
//                list.add("Select  district")
//                for(a in data.indices){
//                    val jObject = data[a]
//                    val hash = HashMap<String, String>()
//                    hash["id"] = jObject.id
//                    hash["name"] = jObject.name
//
//                    arrayListDistrict.add(hash)
//                    list.add(jObject.name)
//
//                }
//
//                if(arrayListDistrict.size>0){
//                    val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner_list, list)
//                    adapter.setDropDownViewResource(R.layout.layout_dropdown)
//                    binding.spinDistrict.adapter = adapter
//                }
//            }
//        }
    }
    fun getDistrctFromRegion(districts: String){
        try {
            val jsonArray =JSONArray(districts)
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
            }
        }catch (e: Exception){
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
         * @return A new instance of fragment NewAttendance.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewAttendance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}