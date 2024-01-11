package com.dawolf.yea.fragments.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.databinding.FragmentShowAgentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShowAgent.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowAgent : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentShowAgentBinding

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
        val view = inflater.inflate(R.layout.fragment_show_agent, container, false)
        binding = FragmentShowAgentBinding.bind(view)

        binding.btnViewAttendance.setOnClickListener {

            (activity as MainBase).navTo(AgentAttendance(), "Agent Attendance", "Details", 1)
        }
        getButtons()
        return view

    }

    private fun getButtons() {
        var hash = (activity as MainBase).arrayListAgent
        binding.txtRfid.text = hash["rfid_no"]
        binding.txtName.text = hash["name"]
        binding.txtDob.text = hash["dob"]
        binding.txtPhone.text = hash["phone"]
        binding.txtAddress.text = hash["address"]
        binding.txtAgentId.text = hash["agent_id"]
        binding.txtRegion.text = hash["region_name"]
        binding.txtSupervisor.text = hash["sName"]
        binding.txtDistrict.text = hash["district_name"]
        binding.txtGPS.text = hash["longitude"]+"/"+hash["latitude"]
        binding.txtGender.text = hash["gender"]
        binding.txtRegister.text = hash["date"]
        binding.txtStatus.text = hash["status"]
        if(hash["status"]=="Active"){
            binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.green))
        }else{
            binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.red))

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShowAgent.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShowAgent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainBase).binding.txtTopic.text = "Details"
    }
}