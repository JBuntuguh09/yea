package com.dawolf.yea.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.R
import com.dawolf.yea.resources.Storage

class RecyclerViewSend(context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewSend.MyHolder>() {

    private var arrayList = ArrayList<HashMap<String, String>>()
    private var context : Context
    private var storage : Storage


    init {
        this.arrayList =arrayList
        this.context = context
        this.storage = Storage(context)
    }

    inner class MyHolder (itemview: View) : RecyclerView.ViewHolder(itemview) {
        val rfid : TextView
        val type : TextView
        val status: TextView




        init {
            rfid = itemview.findViewById(R.id.txtRfid)
            type = itemview.findViewById(R.id.txtType)
            status = itemview.findViewById(R.id.txtStatus)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewSend.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_send, parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewSend.MyHolder, position: Int) {
        val hash = arrayList[position]
       // holder.num.text = (position+1).toString()
        holder.rfid.text = hash["rfid"]
        holder.type.text = hash["type"]
        holder.status.text = hash["code"]

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}