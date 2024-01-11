package com.dawolf.yea.fragments.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.adapters.RecyclerViewAttendance
import com.dawolf.yea.databinding.FragmentAgentAttendanceBinding
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
 * Use the [AgentAttendance.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgentAttendance : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAgentAttendanceBinding
    private lateinit var storage: Storage
    private var arrayList = ArrayList<HashMap<String, String>>()

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
        val view =  inflater.inflate(R.layout.fragment_agent_attendance, container, false)
        binding = FragmentAgentAttendanceBinding.bind(view)
        storage = Storage(requireContext())


        getAttendance()
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getAttendance(){
        arrayList.clear()
        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {

                val res = api.getAPI(Constant.URL+"api/attendances",  requireActivity())
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
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["rfid_no"] = jObject.optString("rfid_no")
                hash["name"] = jObject.getJSONObject("agent").optString("name")
                var sigOut =  ""
                if (jObject.optString("signout_date")== ""){
                    sigOut = ShortCut_To.convertDateFormat2(jObject.optString("signout_date"))
                }
                hash["signout"] = sigOut

                hash["signout_by"] = jObject.optString("signout_by")
                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
                hash["supervisor_id"] = jObject.optString("supervisor_id")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))

                hash["region_name"] = jObject.getJSONObject("region").optString("name")
                hash["region_id"] = jObject.getJSONObject("region").optString("region_id")
                hash["created_at"] = jObject.getJSONObject("district").optString("name")
                hash["created_at"] = jObject.getJSONObject("district").optString("district_id")
                hash["sName"] = jObject.getJSONObject("supervisor").optString("name")
                hash["sEmail"] = jObject.getJSONObject("supervisor").optString("email")
                hash["sNumber"] = jObject.getJSONObject("supervisor").optString("phone")
                hash["aName"] = jObject.getJSONObject("agent").optString("name")
                hash["aNumber"] = jObject.getJSONObject("agent").optString("phone")

                if(jObject.optString("rfid_no")== (activity as MainBase).arrayListAgent["rfid_no"]) {
                    arrayList.add(hash)
                }

            }

            val recyclerViewAttendance = RecyclerViewAttendance(requireContext(), arrayList)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.recycler.layoutManager = linearLayoutManager
            binding.recycler.itemAnimator = DefaultItemAnimator()
            binding.recycler.adapter = recyclerViewAttendance


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
         * @return A new instance of fragment AgentAttendance.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentAttendance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainBase).binding.txtTopic.text = "Agent Attendance"
    }
}