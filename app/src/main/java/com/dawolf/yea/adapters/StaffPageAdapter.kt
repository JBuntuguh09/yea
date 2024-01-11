package com.dawolf.yea.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dawolf.yea.fragments.agent.ViewAgents
import com.dawolf.yea.fragments.supervisor.ViewSupervisors

class StaffPageAdapter(fm: FragmentActivity): FragmentStateAdapter(fm) {

    fun getItemName(position: Int):String{
        return when (position){
            0->"Agents"
            1->"Supervisors"

            else->""
        }
    }
    override fun getItemCount(): Int {
        return 2
    }



    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> ViewAgents()
            1->ViewSupervisors()

            else -> ViewAgents()
        }
    }

}