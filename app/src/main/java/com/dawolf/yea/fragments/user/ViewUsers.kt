package com.dawolf.yea.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.adapters.RecyclerViewSupervisors
import com.dawolf.yea.adapters.RecyclerViewUsers
import com.dawolf.yea.database.users.UserViewModel
import com.dawolf.yea.database.users.Users
import com.dawolf.yea.databinding.FragmentViewUsersBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewUsers.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewUsers : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentViewUsersBinding
    private lateinit var storage: Storage
    private  var arrayList= ArrayList<HashMap<String, String>>()
    private lateinit var userViewModel: UserViewModel


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
        val view = inflater.inflate(R.layout.fragment_view_users, container, false)
        binding = FragmentViewUsersBinding.bind(view)
        storage = Storage(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), defaultViewModelProviderFactory)[UserViewModel::class.java]

        getUsers()
        getButtons()
        return view
    }

    private fun getButtons() {
        binding.floatAdd.setOnClickListener {
            (activity as MainBase).navTo(New_User(), "Register New User", "View Users", 1)
        }

        userViewModel.liveData.observe(requireActivity()){data->
            if(data.isNotEmpty()){
                println("not empty")
                arrayList.clear()
                for(a in data.indices){
                    val jObject = data[a]
                    val hash= HashMap<String, String>()

                    hash["id"] = jObject.id
                    hash["name"] = jObject.name
                    hash["phone"] = jObject.phone
                    hash["email"] = jObject.email
                    hash["region_id"] = jObject.regionId
                    hash["district_id"] = jObject.districtId

                    hash["date"] = ShortCut_To.convertDateFormat(jObject.created_at)

                    arrayList.add(hash)

                }


            }
            val recyclerViewUsers = RecyclerViewUsers(requireContext(), arrayList)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.recycler.layoutManager = linearLayoutManager
            binding.recycler.itemAnimator = DefaultItemAnimator()
            binding.recycler.adapter = recyclerViewUsers
        }
    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun getUsers(){

        //binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/registers",  requireActivity())
                withContext(Dispatchers.Main){

                    setSuperInfo(res)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setSuperInfo(res: String) {
        try {
            println("dddddddddddddddddddddddddddddddddd")

            binding.progressBar.visibility = View.GONE


            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            arrayList.clear()
            println("mmmmmmmmmmmmmmmmmmmmmm")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["name"] = jObject.optString("name")
                hash["phone"] = jObject.optString("phone")
                hash["email"] = jObject.optString("email")
                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
//                hash["email_verified_at"] = jObject.optString("email_verified_at")
//                hash["changed_password_at"] = jObject.optString("changed_password_at")
////                hash["region_name"] = jObject.optJSONObject("region").optString("name")
////                hash["region_id"] = jObject.optJSONObject("region").optString("region_id")
////                hash["district_name"] = jObject.optJSONObject("district").optString("name")
////                hash["district_id"] = jObject.optJSONObject("district").optString("district_id")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))

//                arrayList.add(hash)

                val user = Users(jObject.optString("id"), jObject.optString("name"), jObject.optString("phone"),
                    jObject.optString("email"), jObject.optString("region_id"), jObject.optString("district_id"), jObject.optString("created_at"))

                userViewModel.insert(user)

//                val recyclerViewUsers = RecyclerViewUsers(requireContext(), arrayList)
//                val linearLayoutManager = LinearLayoutManager(requireContext())
//                binding.recycler.layoutManager = linearLayoutManager
//                binding.recycler.itemAnimator = DefaultItemAnimator()
//                binding.recycler.adapter = recyclerViewUsers

            }




        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainBase).binding.txtTopic.text="View Users"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewUsers.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewUsers().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}