package com.dawolf.yea.fragments.attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.adapters.RecyclerViewAttendance
import com.dawolf.yea.database.Attendances.AttendancesViewModel
import com.dawolf.yea.databinding.FragmentAttendancePeriodBinding
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AttendancePeriod.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendancePeriod : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAttendancePeriodBinding
    private lateinit var storage: Storage
    private lateinit var attendancesViewModel: AttendancesViewModel
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
        val view = inflater.inflate(R.layout.fragment_attendance_period, container, false)
        binding = FragmentAttendancePeriodBinding.bind(view)
        storage = Storage(requireContext())

        attendancesViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AttendancesViewModel::class.java]

        getAttendance()
        return view
    }

    private fun getAttendance() {
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

        attendancesViewModel.liveData.observe(requireActivity()){data->
            try {
                if(data.isNotEmpty()){
                    try {
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
                            //hash["sEmail"] = jObject.s
                            // hash["sNumber"] = jObject.getJSONObject("supervisor").optString("phone")
                            //hash["aName"] = jObject.getJSONObject("agent").optString("name")
                            hash["agent_id"] = jObject.agent_id


                            if (storage.period == "day" && ShortCut_To.currentDates == ShortCut_To.convertDateFormat3(jObject.created_at)){
                                arrayList.add(hash)
                            }else if (storage.period == "week" && ShortCut_To.isSameWeek(ShortCut_To.currentDates, ShortCut_To.convertDateFormat3(jObject.created_at))){
                                arrayList.add(hash)
                            }else if (storage.period == "month" && ShortCut_To.isSameMonth(ShortCut_To.currentDates, ShortCut_To.convertDateFormat3(jObject.created_at))){
                                arrayList.add(hash)
                            }


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
            }catch (_: Exception){

            }
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AttendancePeriod.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AttendancePeriod().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if(storage.period.equals("day")){
            (activity as MainBase).binding.txtTopic.text = "Today's Attendance"
        }else if(storage.period.equals("week")){
            (activity as MainBase).binding.txtTopic.text = "This week's Attendance"
        }else if(storage.period.equals("day")){
            (activity as MainBase).binding.txtTopic.text = "This Month's Attendance"
        }
    }
}