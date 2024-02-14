package com.dawolf.yea.fragments.supervisor

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
import com.dawolf.yea.adapters.RecyclerViewSupervisors
import com.dawolf.yea.database.supervisor.Supervisor
import com.dawolf.yea.database.supervisor.SupervisorViewModel
import com.dawolf.yea.databinding.FragmentSupervisorsBinding
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
 * Use the [Supervisors.newInstance] factory method to
 * create an instance of this fragment.
 */
class Supervisors : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSupervisorsBinding
    private lateinit var storage: Storage
    private  var arrayList= ArrayList<HashMap<String, String>>()
    private lateinit var supervisorViewModel: SupervisorViewModel

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
        val view = inflater.inflate(R.layout.fragment_supervisors, container, false)
        binding = FragmentSupervisorsBinding.bind(view)
        storage = Storage(requireContext())
        supervisorViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[SupervisorViewModel::class.java]

        getOffline()

        getSupervisors()
        getButtons()
        return view

    }

    private fun getOffline() {
        supervisorViewModel.liveData.observe(requireActivity()){data->
            try {
                if(data.isNotEmpty()){
                    arrayList.clear()
                    for (a in data.indices){
                        val hash = HashMap<String, String>()
                        val jObject = data[a]

                        hash["id"] = jObject.id
                        hash["supervisor_id"] = jObject.supervisor_id
                        hash["name"] = jObject.name
                        hash["status"] = jObject.status
                        hash["phone"] = jObject.phone
                        hash["region_id"] = jObject.region_id
                        hash["district_id"] = jObject.district_id
                        hash["created_at"] = jObject.created_at
                        hash["region_name"] = jObject.region_name

                        hash["district_name"] = jObject.district_name

                        hash["date"] = ShortCut_To.convertDateFormat(jObject.created_at)

                        arrayList.add(hash)

                    }

                }
                val recyclerViewSupervisors = RecyclerViewSupervisors(requireContext(), arrayList)
                val linearLayoutManager = LinearLayoutManager(requireContext())
                binding.recycler.layoutManager = linearLayoutManager
                binding.recycler.itemAnimator = DefaultItemAnimator()
                binding.recycler.adapter = recyclerViewSupervisors
            }catch (e:Exception){

            }
        }

        ShortCut_To.runSwipe(binding.swipe){
            getSupervisors()
        }
    }

    private fun getButtons() {
        binding.floatAdd.setOnClickListener {
            (activity as MainBase).navTo(RegisterSupervisor(), "New Team Leader", "Team Leaders", 1)
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
                || hash["status"]!!.lowercase().contains(binding.edtSearch.text.toString().lowercase())){
                searchArray.add(hash)
            }
        }

        //ShortCut_To.sortData(arrayList, "name")
        val recyclerViewSupervisors = RecyclerViewSupervisors(requireContext(), searchArray)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.itemAnimator = DefaultItemAnimator()
        binding.recycler.adapter = recyclerViewSupervisors

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getSupervisors(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/supervisor/user/${storage.uSERID!!}",  requireActivity())
                withContext(Dispatchers.Main){

                    setSuperInfo(res)
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

    private fun setSuperInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            supervisorViewModel.deleteSuper(storage.uSERID!!)
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["supervisor_id"] = jObject.optString("supervisor_id")
                hash["name"] = jObject.optString("name")
                hash["status"] = jObject.optString("status")
                hash["phone"] = jObject.optString("phone")
                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
                hash["created_at"] = jObject.optString("created_at")
                hash["region_name"] = jObject.getJSONObject("region").optString("name")
                hash["region_id"] = jObject.getJSONObject("region").optString("region_id")
                hash["district_name"] = jObject.getJSONObject("district").optString("name")
                hash["district_id"] = jObject.getJSONObject("district").optString("district_id")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))

                // arrayList.add(hash)
                val supervisor = Supervisor(jObject.getString("supervisor_id"), storage.uSERID!!, jObject.getString("id"), jObject.getString("name"),
                    jObject.getString("phone"), jObject.getString("status"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id")
                    , jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"), jObject.getString("created_at"))

                supervisorViewModel.insert(supervisor)

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
         * @return A new instance of fragment Supervisors.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Supervisors().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainBase).binding.txtTopic.text = "Team Leaders"
    }
}