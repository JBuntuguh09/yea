package com.dawolf.yea.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.fragments.agent.ShowAgent
import com.dawolf.yea.resources.Storage

class RecyclerViewCodes (context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewCodes.MyHolder>() {

    private var arrayList = ArrayList<HashMap<String, String>>()
    private var context : Context
    private var storage : Storage


    init {
        this.arrayList =arrayList
        this.context = context
        this.storage = Storage(context)
    }

    inner class MyHolder (itemview: View) : RecyclerView.ViewHolder(itemview) {
        val num : TextView
        val code : TextView

        val card: LinearLayout




        init {
            num = itemview.findViewById(R.id.txtNum)
            code = itemview.findViewById(R.id.txtCode)

            card = itemview.findViewById(R.id.linMain)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewCodes.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_codes,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewCodes.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.num.text = (position+1).toString()
        holder.code.text = hash["rfid"]
       // holder.card


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}