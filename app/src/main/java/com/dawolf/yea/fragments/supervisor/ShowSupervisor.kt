package com.dawolf.yea.fragments.supervisor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.database.supervisor.SupervisorViewModel
import com.dawolf.yea.databinding.FragmentShowSupervisorBinding
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShowSupervisor.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowSupervisor : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentShowSupervisorBinding
    private lateinit var storage: Storage
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
        val view = inflater.inflate(R.layout.fragment_show_supervisor, container, false)
        binding = FragmentShowSupervisorBinding.bind(view)
        supervisorViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[SupervisorViewModel::class.java]


        getWatchers()
        return view
    }

    fun getWatchers(){
        val id = (activity as MainBase).arrayListSuper["supervisor_id"]!!
        supervisorViewModel.getSuper(id).observe(requireActivity()){data->
            if (data.isNotEmpty()){
                val hash = data[0]

                binding.txtSupervisorid.text = hash.supervisor_id
                binding.txtName.text = hash.name
                binding.txtPhone.text = hash.phone
                binding.txtRegion.text = hash.region_name
                binding.txtDistrict.text = hash.district_name
                binding.txtRegister.text = ShortCut_To.convertDateFormat(hash.created_at)
                binding.txtStatus.text = hash.status
                if(hash.status=="Active"){
                    binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.green))
                }else{
                    binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.red))

                }
            }
        }
    }

    private fun getButtons() {
        var hash = (activity as MainBase).arrayListSuper
        binding.txtSupervisorid.text = hash["supervisor_id"]
        binding.txtName.text = hash["name"]
        binding.txtPhone.text = hash["phone"]
        binding.txtRegion.text = hash["region_name"]
        binding.txtDistrict.text = hash["district_name"]
        binding.txtRegister.text = ShortCut_To.convertDateFormat(hash["date"]!!)
        binding.txtStatus.text = hash["status"]
        if(hash["status"]=="Active"){
            binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.green))
        }else{
            binding.txtStatus.setTextColor(requireActivity().resources.getColor(R.color.red))

        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainBase).binding.txtTopic.text = "Details"
        (activity as MainBase).binding.imgEdit.visibility = View.VISIBLE
        (activity as MainBase).binding.imgEdit.setOnClickListener {
            (activity as MainBase).binding.imgEdit.visibility = View.GONE
            (activity as MainBase).navTo(EditSupervisor(), "Edit Team Leader", "Details", 1)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShowSupervisor.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShowSupervisor().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}