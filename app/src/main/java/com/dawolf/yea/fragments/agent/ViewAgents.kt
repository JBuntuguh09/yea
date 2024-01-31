package com.dawolf.yea.fragments.agent

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
import com.dawolf.yea.adapters.RecyclerViewAgents
import com.dawolf.yea.database.agent.Agent
import com.dawolf.yea.database.agent.AgentViewModel
import com.dawolf.yea.databinding.FragmentViewAgentsBinding
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
 * Use the [ViewAgents.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAgents : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentViewAgentsBinding
    private lateinit var storage: Storage
    private  var arrayList= ArrayList<HashMap<String, String>>()
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
        val view = inflater.inflate(R.layout.fragment_view_agents, container, false)
        binding = FragmentViewAgentsBinding.bind(view)
        agentViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AgentViewModel::class.java]
        storage = Storage(requireActivity())


        getAgents()
        getButtons()
        return view
    }
    private fun getButtons() {
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

        binding.floatAdd.setOnClickListener {
            storage.randVal = ""
            (activity as MainBase).navTo(RegisterAgent(), "New Beneficiary", "Staff", 1)
        }

        agentViewModel.getAgent(storage.uSERID!!).observe(requireActivity()){data->
            try {
                if(data.isNotEmpty()){
                    arrayList.clear()
                    for(a in data.indices){
                        val jObject = data[a]

                        val hash = HashMap<String, String>()

                        hash["id"] = jObject.id
                        hash["rfid_no"] = jObject.rfid_no
                        hash["agent_id"] = jObject.agent_id
                        hash["name"] = jObject.name
                        hash["status"] = jObject.status
                        hash["gender"] = jObject.gender
                        hash["dob"] = jObject.dob
                        hash["phone"] = jObject.phone
                        hash["address"] = jObject.address
                        hash["region_id"] = jObject.region_id
                        hash["district_id"] = jObject.district_id
                        hash["latitude"] = jObject.latitude
                        hash["longitude"] = jObject.longitude
                        hash["supervisor_id"] = jObject.supervisor_id
                        hash["date"] = ShortCut_To.convertDateFormat(jObject.created_at)
                        hash["sort"] = ShortCut_To.convertForSort(jObject.created_at)

                        hash["region_name"] = jObject.region_name
                        hash["district_name"] = jObject.district_name
                        hash["sName"] = jObject.supervisor_name
                        hash["sEmail"] = jObject.supervisor_email
                        hash["sNumber"] = jObject.supervisor_phone

                        arrayList.add(hash)

                    }

                    ShortCut_To.sortDataInvert(arrayList, "sort")
                    val recyclerViewAgents = RecyclerViewAgents(requireContext(), arrayList)
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    binding.recycler.layoutManager = linearLayoutManager
                    binding.recycler.itemAnimator = DefaultItemAnimator()
                    binding.recycler.adapter = recyclerViewAgents
                }
            }catch (_: Exception){

            }

        }
    }

    private fun searchData() {
        val searchArray = ArrayList<HashMap<String, String>>()
        for (a in arrayList.indices){
            val hash = arrayList[a]
            if(hash["name"]!!.lowercase().contains(binding.edtSearch.text.toString().lowercase())
                || hash["status"]!!.lowercase().contains(binding.edtSearch.text.toString().lowercase())){
                    searchArray.add(hash)
                }
        }

        //ShortCut_To.sortData(arrayList, "name")
        val recyclerViewAgents = RecyclerViewAgents(requireContext(), searchArray)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.itemAnimator = DefaultItemAnimator()
        binding.recycler.adapter = recyclerViewAgents

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getAgents(){
        arrayList.clear()
        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {

                val res = api.getAPI(Constant.URL+"api/agent/user/${storage.uSERID!!}",  requireActivity())
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
            arrayList.clear()
            agentViewModel.deleteAgent(storage.uSERID!!)
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["rfid_no"] = jObject.optString("rfid_no")
                hash["agent_id"] = jObject.optString("agent_id")
                hash["name"] = jObject.optString("name")
                hash["status"] = jObject.optString("status")
                hash["dob"] = jObject.optString("dob")
                hash["gender"] = jObject.optString("gender")
                hash["phone"] = jObject.optString("phone")
                hash["address"] = jObject.optString("address")
                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
                hash["latitude"] = jObject.optString("latitude")
                hash["longitude"] = jObject.optString("longitude")
                hash["supervisor_id"] = jObject.optString("supervisor_id")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))

                hash["region_name"] = jObject.getJSONObject("region").optString("name")
                hash["district_name"] = jObject.getJSONObject("district").optString("name")
                hash["sName"] = jObject.getJSONObject("supervisor").optString("name")
                hash["sEmail"] = jObject.getJSONObject("supervisor").optString("email")
                hash["sNumber"] = jObject.getJSONObject("supervisor").optString("phone")


                val agent = Agent(jObject.getString("rfid_no"), storage.uSERID!!, jObject.getString("id"), jObject.getString("agent_id"),
                    jObject.getString("name"), jObject.getString("dob"), jObject.getString("phone"), jObject.getString("address"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id")
                    , jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"),
                    jObject.getString("rfid_no"), jObject.getString("longitude"),
                    jObject.getJSONObject("supervisor").getString("name") ,jObject.getString("supervisor_id"),
                    jObject.getJSONObject("supervisor").getString("email"), jObject.getJSONObject("supervisor").getString("phone"),
                    jObject.getString("status"), jObject.getString("created_at"), jObject.getString("updated_at"), jObject.getString("gender"))

                agentViewModel.insert(agent)

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
         * @return A new instance of fragment ViewAgents.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewAgents().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}