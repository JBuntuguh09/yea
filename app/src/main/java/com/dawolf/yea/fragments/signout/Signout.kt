package com.dawolf.yea.fragments.signout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.R
import com.dawolf.yea.adapters.RecyclerViewSign
import com.dawolf.yea.adapters.RecyclerViewSupervisors
import com.dawolf.yea.adapters.RecyclerViewUsers
import com.dawolf.yea.databinding.FragmentSignoutBinding
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
 * Use the [Signout.newInstance] factory method to
 * create an instance of this fragment.
 */
class Signout : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSignoutBinding
    private lateinit var storage: Storage
    private  var arrayList= ArrayList<HashMap<String, String>>()

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
        val view =  inflater.inflate(R.layout.fragment_signout, container, false)
        binding = FragmentSignoutBinding.bind(view)
        storage = Storage(requireContext())


        getSignouts()
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getSignouts(){

        binding.progressBar.visibility = View.VISIBLE
        val api = API()
        GlobalScope.launch {
            try {
                val res = api.getAPI(Constant.URL+"api/signouts",  requireActivity())
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
            binding.progressBar.visibility = View.GONE
            val jsonObject = JSONObject(res)
            val data = jsonObject.getJSONArray("data")
            for(a in 0 until data.length()){
                val jObject = data.getJSONObject(a)
                val hash = HashMap<String, String>()

                hash["id"] = jObject.optString("id")
                hash["rfid_no"] = jObject.optString("rfid_no")
                hash["name"] = jObject.getJSONObject("agent").getString("name")
                hash["sign"] = jObject.optString("signout_date")

                hash["region_id"] = jObject.optString("region_id")
                hash["district_id"] = jObject.optString("district_id")
                hash["created_at"] = jObject.optString("created_at")
                hash["region_name"] = jObject.getJSONObject("region").optString("name")
                hash["district_name"] = jObject.getJSONObject("district").optString("name")
                hash["date"] = ShortCut_To.convertDateFormat(jObject.optString("created_at"))



                arrayList.add(hash)

            }

            val recyclerViewSign = RecyclerViewSign(requireContext(), arrayList)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.recycler.layoutManager = linearLayoutManager
            binding.recycler.itemAnimator = DefaultItemAnimator()
            binding.recycler.adapter = recyclerViewSign


        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Signout.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Signout().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}