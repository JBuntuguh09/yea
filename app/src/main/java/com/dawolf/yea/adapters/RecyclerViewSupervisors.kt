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
import com.dawolf.yea.fragments.agent.ShowAgent
import com.dawolf.yea.fragments.supervisor.ShowSupervisor
import com.dawolf.yea.resources.Storage

class RecyclerViewSupervisors(context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewSupervisors.MyHolder>() {

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
        val phone : TextView
        val status : TextView
        val register: TextView
        val card : CardView
        val label: TextView




        init {
            name = itemview.findViewById(R.id.txtName)
            phone = itemview.findViewById(R.id.txtPhone)
            status = itemview.findViewById(R.id.txtStatus)
            card = itemview.findViewById(R.id.cardMain)
            register = itemview.findViewById(R.id.txtRegister)
            label = itemview.findViewById(R.id.txtSupervisor)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewSupervisors.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_agent,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewSupervisors.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.name.text = hash["name"]
        holder.phone.text = hash["phone"]
        holder.status.text = hash["status"]
        holder.register.text = hash["date"]
        holder.label.text = "Registration Date"

        if(hash["status"]=="Active"){
            holder.status.setTextColor(context.resources.getColor(R.color.green))
        }else{
            holder.status.setTextColor(context.resources.getColor(R.color.red))

        }
        holder.card.setOnClickListener {
            (context as MainBase).arrayListSuper = hash
            (context as MainBase).navTo(ShowSupervisor(), "Details", "View Supervisor", 1)
        }




    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}