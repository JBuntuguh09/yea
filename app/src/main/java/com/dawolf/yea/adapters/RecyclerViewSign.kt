package com.dawolf.yea.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.R
import com.dawolf.yea.resources.Storage

class RecyclerViewSign(context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewSign.MyHolder>() {

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
        val email : TextView




        init {
            name = itemview.findViewById(R.id.txtName)
            phone = itemview.findViewById(R.id.txtPhone)
            email = itemview.findViewById(R.id.txtEmail)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewSign.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_sign,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewSign.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.name.text = hash["name"]
        holder.phone.text = hash["sign"]
        holder.email.text = hash["region_name"]



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}