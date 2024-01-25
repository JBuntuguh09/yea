package com.dawolf.yea.fragments.attendance

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.Startpage
import com.dawolf.yea.adapters.RecyclerViewAgents
import com.dawolf.yea.adapters.RecyclerViewAttendance
import com.dawolf.yea.adapters.RecyclerViewSupervisors
import com.dawolf.yea.database.Attendances.Attendances
import com.dawolf.yea.database.Attendances.AttendancesViewModel
import com.dawolf.yea.databinding.FragmentViewAttendanceBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewAttendance.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAttendance : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentViewAttendanceBinding
    private lateinit var storage: Storage
    private var arrayList = ArrayList<HashMap<String, String>>()
    private lateinit var attendancesViewModel: AttendancesViewModel

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
        val view = inflater.inflate(R.layout.fragment_view_attendance, container, false)
        binding = FragmentViewAttendanceBinding.bind(view)
        storage = Storage(requireContext())
        attendancesViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AttendancesViewModel::class.java]

        getButtons()
        getAttendance()
        getOffline()
        return view
    }

    private fun getOffline() {
        attendancesViewModel.liveData.observe(requireActivity()){data->
            if(data.isNotEmpty()){
                try {
                    arrayList.clear()
                    for(a in data.indices){
                        val hash = HashMap<String, String>()
                        val jObject = data[a]
                        hash["id"] = jObject.id
                        hash["rfid_no"] = jObject.rfid_id
                        hash["name"] = jObject.agent_name
                        hash["signout"] = ShortCut_To.convertDateFormat2(jObject.signout_date)
                        hash["signout_by"] = jObject.signout_by
                        hash["region_id"] = jObject.region_id
                        hash["district_id"] = jObject.district_id
                        hash["supervisor_id"] = jObject.supervisor_id
                        hash["date"] = ShortCut_To.convertDateFormat(jObject.created_at)
                        hash["sort"] = ShortCut_To.convertForSort(jObject.created_at)

                        hash["region_name"] = jObject.region_name

                        hash["district_name"] = jObject.district_name

                        hash["sName"] = jObject.supervisor_name

                        hash["agent_id"] = jObject.agent_id

                        hash["sDate"] =  hash["date"]!!.split(" at ")[0]



                        arrayList.add(hash)


                    }
                    ShortCut_To.sortDataInvert(arrayList, "sort")

                    val recyclerViewAttendance = RecyclerViewAttendance(requireContext(), arrayList)
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    binding.recycler.layoutManager = linearLayoutManager
                    binding.recycler.itemAnimator = DefaultItemAnimator()
                    binding.recycler.adapter = recyclerViewAttendance

                }catch (e: Exception ){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getButtons() {
        binding.floatAdd.setOnClickListener {
            //(activity as MainBase).navTo(NewAttendance(), "New Attendance", "View Attendance", 1)
            val intent = Intent(requireContext(), Startpage::class.java)
            startActivity(intent)
        }

        binding.edtSearch.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtSearch.right - binding.edtSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    // your action here
                    searchData()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun searchData() {
        val searchArray = ArrayList<HashMap<String, String>>()
        for (a in arrayList.indices){
            val hash = arrayList[a]
            if(hash["name"]!!.lowercase().contains(binding.edtSearch.text.toString().lowercase())
                || hash["sName"]!!.lowercase().contains(binding.edtSearch.text.toString().lowercase())){
                searchArray.add(hash)
            }
        }


        val recyclerViewAttendance = RecyclerViewAttendance(requireContext(), searchArray)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.itemAnimator = DefaultItemAnimator()
        binding.recycler.adapter = recyclerViewAttendance

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getAttendance(){
        arrayList.clear()
        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {

                val res = api.getAPI(Constant.URL+"api/attendance/user/${storage.uSERID!!}",  requireActivity())
                withContext(Dispatchers.Main){
                    println(res)
                    setAgentInfo(res)
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

    private fun setAgentInfo(res: String) {
        try {
            var num = 0
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            attendancesViewModel.deleteBid(storage.uSERID!!)
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["rfid_no"] = jObject.optString("rfid_no")
                hash["name"] = jObject.getJSONObject("agent").optString("name")
                hash["signout"] = ShortCut_To.convertDateFormat2(jObject.optString("signout_date"))
                hash["signout_by"] = jObject.optString("signout_by")
                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
                hash["supervisor_id"] = jObject.optString("supervisor_id")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))

                hash["region_name"] = jObject.getJSONObject("region").optString("name")

                hash["district_name"] = jObject.getJSONObject("district").optString("name")

                hash["sName"] = jObject.getJSONObject("supervisor").optString("name")
//                hash["sEmail"] = jObject.getJSONObject("supervisor").optString("email")
//                hash["sNumber"] = jObject.getJSONObject("supervisor").optString("phone")
//
                hash["agent_id"] = jObject.getJSONObject("agent").optString("id")

               // arrayList.add(hash)
                val attendances = Attendances(jObject.getString("id"), storage.uSERID!!,jObject.getString("rfid_no"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id"),
                    jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"),
                    jObject.getJSONObject("supervisor").getString("name"), jObject.getJSONObject("supervisor").getString("id"),
                    jObject.getJSONObject("agent").getString("name"),  jObject.getJSONObject("agent").getString("id"),
                    jObject.getString("signout_date"), jObject.getString("signout_by"), jObject.getString("created_at"))
                attendancesViewModel.insert(attendances)

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
         * @return A new instance of fragment ViewAttendance.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewAttendance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        (activity as MainBase).binding.txtTopic.text = "View Attendance"
        super.onResume()
    }
}