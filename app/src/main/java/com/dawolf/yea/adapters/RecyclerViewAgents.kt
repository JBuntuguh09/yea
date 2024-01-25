package com.dawolf.yea.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.fragments.agent.ShowAgent
import com.dawolf.yea.resources.Storage

class RecyclerViewAgents(context: Context, arrayList: ArrayList<HashMap<String, String>>):RecyclerView.Adapter<RecyclerViewAgents.MyHolder>() {

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
        val reg: TextView
        val card: CardView




        init {
            name = itemview.findViewById(R.id.txtName)
            phone = itemview.findViewById(R.id.txtPhone)
            status = itemview.findViewById(R.id.txtStatus)
            reg = itemview.findViewById(R.id.txtRegister)
            card = itemview.findViewById(R.id.cardMain)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAgents.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_agent,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAgents.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.name.text = hash["name"]
        holder.phone.text = hash["phone"]
        holder.status.text = hash["status"]
        holder.reg.text = hash["sName"]

        if(hash["status"] == "Active"){
            holder.status.setTextColor(context.resources.getColor(R.color.green))
        }else{
            holder.status.setTextColor(context.resources.getColor(R.color.red))
        }
        holder.card.setOnClickListener {

            (context as MainBase).arrayListAgent = hash
            (context as MainBase).navTo(ShowAgent(), "Details", "View Agent", 1)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}