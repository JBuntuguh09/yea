package com.dawolf.yea.fragments.user

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dawolf.yea.R
import com.dawolf.yea.databinding.FragmentNewUserBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [New_User.newInstance] factory method to
 * create an instance of this fragment.
 */
class New_User : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNewUserBinding
    private lateinit var storage: Storage


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
        val view = inflater.inflate(R.layout.fragment_new__user, container, false)
        binding = FragmentNewUserBinding.bind(view)
        storage = Storage(requireContext())
        getButtons()
        return view
    }

    private fun getButtons() {

        binding.btnSubmit.setOnClickListener {
            if(binding.edtName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter the user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtPhone.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter the user phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtEmail.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter the user email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtPassword.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter the user password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.edtConfirm.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requireActivity().runOnUiThread {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSubmit.isEnabled = false
            }
            sendData()
        }
    }

    private fun sendData() = runBlocking {
        val api = API()
        val body = mapOf(
            "name" to binding.edtName.text.toString(),
            "phone" to binding.edtPhone.text.toString(),
            "email" to binding.edtEmail.text.toString(),
            "password" to binding.edtPassword.text.toString(),
            "password_confirmation" to binding.edtConfirm.text.toString()
        )

        try {
            GlobalScope.launch {
                val res:String = api.postAPIWithHeader(
                    Constant.URL + "api/register",
                    body,
                    requireActivity()
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){
                        Toast.makeText(requireContext(), "Error: Failed to create new user", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.isEnabled = true


                    }else {
                        setInfo(res)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: Failed to create new user", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true


        }

    }

    private fun setInfo(res: String) {
        try {
            val jsonObject = JSONObject(res)
            val mess = jsonObject.optString("message")

            Toast.makeText(requireContext(), mess, Toast.LENGTH_SHORT).show()
            if(mess == "User created successfully"){
//                val alertDialog = AlertDialog.Builder(requireContext())
//                alertDialog.setTitle("Success")
//                alertDialog.setMessage("You have successfully created a new user user. \n" +
//                        "Name : ${binding.edtName.text.toString()}\n" +
//                        "Phone: ${binding.edtPhone.text.toString()}\n" +
//                        "Email: ${binding.edtEmail.text.toString()}\n" +
//                        "")
                requireActivity().onBackPressed()
            }
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: Failed to create new user. Please try again", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSubmit.isEnabled = true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment New_User.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            New_User().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}