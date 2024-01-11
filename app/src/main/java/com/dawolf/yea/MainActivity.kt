package com.dawolf.yea

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.database.login.Login
import com.dawolf.yea.database.login.LoginViewModel
import com.dawolf.yea.databinding.ActivityMainBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: Storage
    private lateinit var loginViewModel: LoginViewModel
    private var arrayListOffline = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Storage(this)
        loginViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[LoginViewModel::class.java]

        loginOffline()
        getButtons()
    }

    private fun loginOffline() {


    }

    private fun getButtons() {
        binding.btnLogin.setOnClickListener {
            if(binding.edtEmail.text.toString().isEmpty()){
                Toast.makeText(this, "Enter your username/phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            runOnUiThread {
//                binding.progressBar.visibility = View.VISIBLE
//                binding.btnLogin.isEnabled = false
//            }
            //loginTo()
            loginTest()
        }
    }

    private fun loginTest() {
        val res = loginViewModel.getUser(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
        res.observe(this) { data ->
            val check = 0
            if (data.isNotEmpty()) {
                    val hash = HashMap<String, String>()
                    val a=0
                    hash["name"] = data[a].name
                    hash["password"] = data[a].password
                    hash["email"] = data[a].email
                    hash["token"] = data[a].token
                    hash["id"] = data[a].id
                    hash["dob"] = data[a].dob
                    hash["phone"] = data[a].phone
                    hash["region_id"] = data[a].regionId
                    hash["district_id"] = data[a].districtId
                    hash["changedPassword"] = data[a].changedPassword
                    hash["emailVerified"] = data[a].emailVerified

                        storage.tokenId = "Bearer ${data[a].token}"
                        storage.token = data[a].token
                        storage.uSERID = data[a].id
                        storage.uSERNAME = data[a].name
                        storage.email = data[a].email
                        storage.pASSWORD = binding.edtPassword.text.toString()
                        try {
                            val intent = Intent(this, MainBase::class.java)
                            startActivity(intent)
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    return@observe
            } else {
                runOnUiThread {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                loginTo()
                return@observe
            }
        }
    }

    private fun loginTo() =runBlocking {
        val api = API()

        val formdata = mapOf(
            "username" to binding.edtEmail.text.toString(),
            "password" to binding.edtPassword.text.toString())


        try {
            GlobalScope.launch {
                val res:String = api.postAPI(
                    Constant.URL + "api/login",
                    formdata,
                    this@MainActivity,
                    binding.constMain
                )

                withContext(Dispatchers.Main){
                    if(res == "[]"){

                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true


                    }else {
                        setInfo(res)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()

            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true


        }

    }

    private fun setInfo(res: String) {

        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true

        try {
            val jsonObject = JSONObject(res)
            if(jsonObject.optString("message").equals("User logged in successfully")){


                val data = jsonObject.getJSONObject("data")
                val token = jsonObject.getJSONObject("meta")["token"].toString()


                val userId = data["id"].toString()
                val username  = data["name"].toString()
                val email  = data["email"].toString()
                val phone  = data["phone"].toString()

                val region_id  = data["region_id"].toString()
                val district_id  = data["district_id"].toString()
                val changed_password_at  = data["changed_password_at"].toString()
                val email_verified_at  = data["email_verified_at"].toString()


                val user = Login(
                    userId, token, username, email,
                    binding.edtPassword.text.toString(),
                    phone, "",region_id, district_id,
                    changed_password_at, email_verified_at, binding.edtEmail.text.toString())

                loginViewModel.insert(user)



                storage.tokenId = "Bearer $token"
                storage.token = token
                storage.uSERID = userId
                storage.uSERNAME = username
                storage.email = email
                storage.pASSWORD = binding.edtPassword.text.toString()


                val intent = Intent(this, MainBase::class.java)
                startActivity(intent)


            }else{

                Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
                var mess = jsonObject.optString("message", "")
                if(mess.isEmpty()){
                    mess = jsonObject.optString("errors", "Error. Please check your credentials")
                }


            }
        }catch (e:Exception){
            println("============"+res)
            Toast.makeText(this, "Error: Please try again", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }
}