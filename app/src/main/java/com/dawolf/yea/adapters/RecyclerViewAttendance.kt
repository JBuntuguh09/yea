package com.dawolf.yea.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.fragments.attendance.ShowAttendance
import com.dawolf.yea.resources.Storage

class RecyclerViewAttendance (context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewAttendance.MyHolder>() {

    private var arrayList = ArrayList<HashMap<String, String>>()
    private var context : Context
    private var storage : Storage


    init {
        this.arrayList =arrayList
        this.context = context
        this.storage = Storage(context)
    }

    inner class MyHolder (itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name : TextView
        val date : TextView
        val signout : TextView
        val supervisor : TextView
        val card : CardView


        init {
            name = itemview.findViewById(R.id.txtName)
            date = itemview.findViewById(R.id.txtDate)
            signout = itemview.findViewById(R.id.txtSignout)
            supervisor = itemview.findViewById(R.id.txtSupervisor)
            card = itemview.findViewById(R.id.cardMain)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAttendance.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_attendance,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAttendance.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.name.text = hash["name"]
        holder.date.text = hash["date"]
        holder.signout.text = hash["signout"]
        holder.supervisor.text = hash["sName"]

        holder.card.setOnClickListener {
            (context as MainBase).arrayListAttend = hash
            (context as MainBase).navTo(ShowAttendance(), "Details", "View Attendance", 1)
        }



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}