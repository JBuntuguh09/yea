package com.dawolf.yea.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.R
import com.dawolf.yea.resources.Storage

class RecyclerViewUsers(context: Context, arrayList: ArrayList<HashMap<String, String>>):
    RecyclerView.Adapter<RecyclerViewUsers.MyHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewUsers.MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_users,parent, false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewUsers.MyHolder, position: Int) {
        val hash = arrayList[position]
        holder.name.text = hash["name"]
        holder.phone.text = hash["phone"]
        holder.email.text = hash["email"]



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}