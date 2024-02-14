package com.dawolf.yea.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.RFIDActivity2
import com.dawolf.yea.Startpage
import com.dawolf.yea.database.Attendances.Attendances
import com.dawolf.yea.database.Attendances.AttendancesViewModel
import com.dawolf.yea.database.agent.Agent
import com.dawolf.yea.database.agent.AgentViewModel
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.supervisor.Supervisor
import com.dawolf.yea.database.supervisor.SupervisorViewModel
import com.dawolf.yea.databinding.FragmentStartPageBinding
import com.dawolf.yea.fragments.agent.Agents
import com.dawolf.yea.fragments.agent.RegisterAgent
import com.dawolf.yea.fragments.attendance.AttendancePeriod
import com.dawolf.yea.fragments.attendance.NewAttendance
import com.dawolf.yea.fragments.attendance.ViewAttendance
import com.dawolf.yea.fragments.present.ViewPresent
import com.dawolf.yea.fragments.send.ViewSend
import com.dawolf.yea.fragments.signout.AttendanceSignout
import com.dawolf.yea.fragments.signout.Signout
import com.dawolf.yea.fragments.supervisor.RegisterSupervisor
import com.dawolf.yea.fragments.supervisor.Supervisors
import com.dawolf.yea.fragments.user.ViewUsers
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentStartPageBinding
    private lateinit var agentViewModel: AgentViewModel
    private lateinit var supervisorViewModel: SupervisorViewModel
    private lateinit var attendancesViewModel: AttendancesViewModel
    private var dates = ArrayList<String>()
    private var counts= ArrayList<Float>()
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
        val view =  inflater.inflate(R.layout.fragment_start_page, container, false)
        binding = FragmentStartPageBinding.bind(view)
        agentViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AgentViewModel::class.java]
        supervisorViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[SupervisorViewModel::class.java]
        attendancesViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[AttendancesViewModel::class.java]
        storage = Storage(requireContext())

        getOffline()
        getButtons()
        getAttendance()
        getAgents()
        getSupervisors()
        ShortCut_To.runSwipe(binding.swipe){
            binding.progressBar.visibility = View.VISIBLE
            getAttendance()
            getAgents()
            getSupervisors()
        }



        return view
    }

    private fun getOffline() {
        agentViewModel.getAgent(storage.uSERID!!).observeOnce(viewLifecycleOwner){data->
            var num= 0
            if (data.isNotEmpty()){
                for(a in data.indices) {
                    val jObject = data[a]
                    if (jObject.status == "Active") {
                        num += 1
                    }
                }
                binding.txtAgent.text = num.toString()

            }
        }


        supervisorViewModel.getSuper(storage.uSERID!!).observeOnce(viewLifecycleOwner){data->
            var num= 0
            if (data.isNotEmpty()){
                for(a in data.indices) {
                    val jObject = data[a]
                    if (jObject.status == "Active") {
                        num += 1
                    }
                }

                binding.txtSuper.text = num.toString()

            }


        }

        attendancesViewModel.getAttendanceById(storage.uSERID!!).observeOnce(viewLifecycleOwner){data->
            var day = 0
            var week = 0
            var month = 0
            binding.progressBar.visibility = View.GONE
            if (data.isNotEmpty()){
                val jsonArray = JSONArray()
                for(a in data.indices) {
                    val jObject = data[a]
                        if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.created_at, "day")){
                            day += 1
                        }

                        if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.created_at, "week")){
                            week += 1
                        }

                        if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.created_at, "month")){
                            month += 1
                        }

                    val jsonObject = JSONObject()
                    jsonObject.put("created_at", jObject.created_at)

                    jsonArray.put(jsonObject)

                }
                try {

                    binding.txtDay.text = day.toString()
                    binding.txtWeek.text = week.toString()
                    binding.txtMonth.text = month.toString()


                    val dateOccurrences = ShortCut_To.extractAndCountCreatedDates(jsonArray.toString())

                    dates.clear()
                    counts.clear()
                    dateOccurrences.forEach { (date, count) ->
                        // println("$date: $count occurrences")
                        if(ShortCut_To.checkifIsSameMonthYear(date)) {
                            dates.add(date)
                            counts.add(count.toFloat())
                        }
                    }

                    showLine()




                }catch (e: Exception){
                    e.printStackTrace()
                }

            }

        }

        val act = (activity as MainBase)
        act.noAttend.observe(requireActivity()){data->
            if(data=="Yes"){
                binding.txtDay.text = "0"
                binding.txtWeek.text = "0"
                binding.txtMonth.text = "0"
            }
        }

        act.noSuper.observe(requireActivity()){data->
            if(data=="Yes"){
                binding.txtSuper.text = "0"

            }
        }

        act.noAgent.observe(requireActivity()){data->
            if(data=="Yes"){
                binding.txtAgent.text = "0"

            }
        }

    }

    private fun getButtons() {
        binding.cardStaff.setOnClickListener {
          //  ShortCut_To.blinkCardView(binding.cardStaff, requireActivity())
            (activity as MainBase).navTo(StaffBase(), "Registration", "Start", 1)
        }

        binding.cardAttendance.setOnClickListener {

            (activity as MainBase).navTo(ViewAttendance(), "Sign Ins", "Start", 1)
        }

        binding.cardUsers.setOnClickListener {

            (activity as MainBase).navTo(ViewUsers(), "Users", "Start", 1)
        }

        binding.cardSignout.setOnClickListener {

            (activity as MainBase).navTo(Signout(), "Sign Out", "Start", 1)
        }

        binding.cardPresent.setOnClickListener {

            (activity as MainBase).navTo(ViewSend(), "Attendance", "Start", 1)
        }

        binding.cardDaily.setOnClickListener {
            if(binding.txtDay.text.toString() == "0"){
                Toast.makeText(requireContext(), "There is no sign ins for today", Toast.LENGTH_SHORT).show()
            }else {
                storage.period = "day"
                (activity as MainBase).navTo(AttendancePeriod(), "Attendance", "Start", 1)
            }

        }
        binding.cardWeekly.setOnClickListener {

            if(binding.txtWeek.text.toString() == "0"){
                Toast.makeText(requireContext(), "There are no sign in for this month", Toast.LENGTH_SHORT).show()
            }else {
                storage.period = "week"
                (activity as MainBase).navTo(AttendancePeriod(), "Sign In", "Start", 1)
            }
        }
        binding.cardMonthly.setOnClickListener {

            if(binding.txtMonth.text.toString() == "0"){
                Toast.makeText(requireContext(), "There are no sign ins for this month", Toast.LENGTH_SHORT).show()
            }else {
                storage.period = "month"
                (activity as MainBase).navTo(AttendancePeriod(), "Sign In", "Start", 1)
            }
        }

//        ShortCut_To.moveButton(binding.btnAttendance, binding.constMain)
//        ShortCut_To.moveButton(binding.btnSignout, binding.constMain)
//
        binding.btnAttendance.setOnClickListener {
            storage.project = "Attendance"
            val intent = Intent(requireContext(), RFIDActivity2::class.java)
            startActivity(intent)
        }
        binding.btnSignout.setOnClickListener {
            storage.project = "Signout"
            val intent = Intent(requireContext(), RFIDActivity2::class.java)
            startActivity(intent)
        }

        binding.cardAgent.setOnClickListener {
            if(binding.txtAgent.text.toString() == "0"){
                storage.randVal = ""
                (activity as MainBase).navTo(RegisterAgent(), "New Agent", "Welcome", 1)
            }else {
                (activity as MainBase).navTo(Agents(), "Agents", "Welcome", 1)
            }
        }

        binding.cardSuper.setOnClickListener {
            if(binding.txtSuper.text.toString() == "0"){
                (activity as MainBase).navTo(RegisterSupervisor(), "New Supervisor", "Welcome", 1)
            }else {
                (activity as MainBase).navTo(Supervisors(), "Supervisors", "Welcome", 1)
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getAgents(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/agent/user/${storage.uSERID!!}",  requireActivity())
                withContext(Dispatchers.Main){

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
            agentViewModel.deleteAgent(storage.uSERID!!)
            val data = jsonObject.getJSONArray("data")

            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                if (jObject.getString("status")=="Active"){
                    num +=1
                }

                val agent = Agent(jObject.getString("rfid_no"), storage.uSERID!!,jObject.getString("id"), jObject.getString("agent_id"),
                    jObject.getString("name"), jObject.getString("dob"), jObject.getString("phone"), jObject.getString("address"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id")
                    , jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"),
                    jObject.getString("rfid_no"), jObject.getString("longitude"),
                    jObject.getJSONObject("supervisor").getString("name") ,jObject.getString("supervisor_id"),
                    jObject.getJSONObject("supervisor").getString("email"), jObject.getJSONObject("supervisor").getString("phone"),
                    jObject.getString("status"), jObject.getString("created_at"), jObject.getString("updated_at"), jObject.getString("gender"))

                    agentViewModel.insert(agent)
            }

            binding.txtAgent.text = num.toString()
        }catch (e: Exception){
            e.printStackTrace()
        }
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
            var num = 0
            val jsonObject = JSONObject(res)
            supervisorViewModel.deleteSuper(storage.uSERID!!)
            val data = jsonObject.getJSONArray("data")

            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                if (jObject.getString("status")=="Active"){
                    num +=1
                }
                val supervisor = Supervisor(jObject.getString("supervisor_id"), storage.uSERID!!,jObject.getString("id"), jObject.getString("name"),
                    jObject.getString("phone"), jObject.getString("status"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id")
                    , jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"), jObject.getString("created_at"))

                supervisorViewModel.insert(supervisor)
            }

            binding.txtSuper.text = num.toString()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getAttendance(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {


                val res = api.getAPI(Constant.URL+"api/attendance/user/${storage.uSERID!!}",  requireActivity())
                withContext(Dispatchers.Main){

                    setAttendInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                   // Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setAttendInfo(res: String) {
        try {
            binding.progressBar.visibility = View.GONE

            var day = 0
            var week = 0
            var month = 0
            val jsonObject = JSONObject(res)
            attendancesViewModel.deleteBid(storage.uSERID!!)
            val data = jsonObject.getJSONArray("data")

            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "day")){
                    day += 1
                }

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "week")){
                    week += 1
                }

                if(ShortCut_To.checkIfWithinDayWeekMonth(jObject.getString("created_at"), "month")){
                    month += 1
                }


                val attendances = Attendances(jObject.getString("id"), storage.uSERID!!,jObject.getString("rfid_no"),
                    jObject.getJSONObject("region").getString("name"), jObject.getString("region_id"),
                    jObject.getJSONObject("district").getString("name"), jObject.getString("district_id"),
                    jObject.getJSONObject("supervisor").getString("name"), jObject.getJSONObject("supervisor").getString("id"),
                    jObject.getJSONObject("agent").getString("name"),  jObject.getJSONObject("agent").getString("id"),
                jObject.getString("signout_date"), jObject.getString("signout_by"), jObject.getString("created_at"))
                attendancesViewModel.insert(attendances)


            }

            binding.txtDay.text = day.toString()
            binding.txtWeek.text = week.toString()
            binding.txtMonth.text = month.toString()



        }catch (e: Exception){
            binding.txtDay.text = "0"
            binding.txtWeek.text = "0"
            binding.txtMonth.text = "0"
            e.printStackTrace()
        }
    }

    private fun showLine(){

        binding.lineChart.clear()
        binding.lineChart.description.isEnabled = false

        binding.lineChart.setTouchEnabled(true)

        binding.lineChart.setDragDecelerationFrictionCoef(0.9f)


        binding.lineChart.setDragEnabled(true)
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.setHighlightPerDragEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately

        // if disabled, scaling can be done on x- and y-axis separately
        binding.lineChart.setPinchZoom(true)

        // set an alternative background color

        // set an alternative background color
        binding.lineChart.setBackgroundColor(Color.LTGRAY)

        // add data
        binding.lineChart.animateX(1500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = binding.lineChart.getLegend()

        // modify the legend ...

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        // l.typeface = tfLight
        l.textSize = 11f
        l.textColor = Color.WHITE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
//        l.setYOffset(11f);



        val leftAxis: YAxis = binding.lineChart.getAxisLeft()
        // leftAxis.typeface = tfLight
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        //leftAxis.axisMaximum = 200f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true

        val rightAxis: YAxis = binding.lineChart.axisRight
        //  rightAxis.typeface = tfLight
        rightAxis.textColor = Color.RED
//        rightAxis.axisMaximum = 900f
//        rightAxis.axisMinimum = -200f
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false

        setData()
        //binding.lineChart.invalidate()
    }

    private fun setData() {
        val values1: ArrayList<Entry> = ArrayList()
        for (i in 0 until counts.size) {
            val `val` = counts[i]

            values1.add(Entry(i.toFloat(), `val`))
        }
//        val values2: ArrayList<Entry> = ArrayList()
//        for (i in 0 until listPay.size) {
//            val `val` = listPay[i]
//            values2.add(Entry(i.toFloat(), `val`))
//        }
//        val values3: ArrayList<Entry> = ArrayList()
//        for (i in 0 until count) {
//            val `val` = (Math.random() * range).toFloat() + 500
//
//            values3.add(Entry(i.toFloat(), `val`))
//        }


        val set1: LineDataSet
        //val set2: LineDataSet
        //val set3: LineDataSet
        if (binding.lineChart.data != null &&
            binding.lineChart.data.dataSetCount > 0
        ) {
            set1 = binding.lineChart.data.getDataSetByIndex(0) as LineDataSet
            //set2 = binding.lineChart.data.getDataSetByIndex(1) as LineDataSet
            //set3 = binding.linChart.data.getDataSetByIndex(2) as LineDataSet
            set1.values = values1
           // set2.values = values2
            // set3.values = values3
            binding.lineChart.data.notifyDataChanged()
            binding.lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values1, "Dates")
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = ColorTemplate.getHoloBlue()
            set1.setCircleColor(Color.WHITE)
            set1.lineWidth = 2f
            set1.circleRadius = 3f
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue()
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
//            set2 = LineDataSet(values2, "Payments")
//            set2.axisDependency = YAxis.AxisDependency.RIGHT
//            set2.color = Color.RED
//            set2.setCircleColor(Color.WHITE)
//            set2.lineWidth = 2f
//            set2.circleRadius = 3f
//            set2.fillAlpha = 65
//            set2.fillColor = Color.RED
//            set2.setDrawCircleHole(false)
//            set2.highLightColor = Color.rgb(244, 117, 117)
            //set2.setFillFormatter(new MyFillFormatter(900f));
//            set3 = LineDataSet(values3, "DataSet 3")
//            set3.axisDependency = AxisDependency.RIGHT
//            set3.color = Color.YELLOW
//            set3.setCircleColor(Color.WHITE)
//            set3.lineWidth = 2f
//            set3.circleRadius = 3f
//            set3.fillAlpha = 65
//            set3.fillColor = ColorTemplate.colorWithAlpha(Color.YELLOW, 200)
//            set3.setDrawCircleHole(false)
//            set3.highLightColor = Color.rgb(244, 117, 117)

            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTextColor(Color.WHITE)
            data.setValueTextSize(9f)

            // set data
            binding.lineChart.data = data

            // Customize the XAxis
            val xAxis: XAxis = binding.lineChart.xAxis


            xAxis.valueFormatter = CustomXAxisValueFormatter(dates) // Set a custom value formatter if needed
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f

            // Refresh the chart
           // binding.lineChart.invalidate()
        }
    }

    class CustomXAxisValueFormatter(private val labels: ArrayList<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            // Ensure that the index is within the array bounds
            val index = value.toInt().coerceIn(0, labels.size - 1)

            return "${labels[index].split("/")[0]}/${labels[index].split("/")[1]}"
        }
    }


    override fun onResume() {
        (activity as MainBase).binding.txtTopic.text = "Welcome"
        storage.randVal = ""
        super.onResume()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StartPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}