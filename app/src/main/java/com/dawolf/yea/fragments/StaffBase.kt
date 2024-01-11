package com.dawolf.yea.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dawolf.yea.R
import com.dawolf.yea.adapters.StaffPageAdapter
import com.dawolf.yea.databinding.FragmentStaffBaseBinding
import com.dawolf.yea.resources.Storage
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StaffBase.newInstance] factory method to
 * create an instance of this fragment.
 */
class StaffBase : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentStaffBaseBinding
    private lateinit var storage: Storage
    private lateinit var tabs : TabLayout

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
        val view = inflater.inflate(R.layout.fragment_staff_base, container, false)
        binding = FragmentStaffBaseBinding.bind(view)

        storage = Storage(requireContext())

        val adapter = StaffPageAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = 0

        tabs = binding.tabLayout
        TabLayoutMediator(tabs, binding.viewPager) { tab, position ->
            tab.text = adapter.getItemName(position)

        }.attach()

        storage.currPage = "Staff"
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StaffBase.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffBase().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}