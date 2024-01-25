package com.dawolf.yea.fragments.signout

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawolf.yea.R
import com.dawolf.yea.adapters.RecyclerViewCodes
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.database.send.Send
import com.dawolf.yea.database.send.SendViewModel
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutViewModel
import com.dawolf.yea.databinding.ActivityAttendanceSignoutBinding
import com.dawolf.yea.databinding.ActivityStartpageBinding
import com.dawolf.yea.resources.Constant
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class AttendanceSignout : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private lateinit var signoutViewModel: SignoutViewModel
    private lateinit var sendViewModel: SendViewModel
    private lateinit var binding: ActivityAttendanceSignoutBinding
    private lateinit var storage: Storage
    val arrayList = ArrayList<HashMap<String, String>>()
    private var b=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_attendance_signout)
        binding = ActivityAttendanceSignoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Storage(this)
        signoutViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SignoutViewModel::class.java]
        sendViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[SendViewModel::class.java]
        getCodes()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {

        } else {
            pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )
        }
    }

    private fun getCodes() {

        binding.btnOnline.setOnClickListener {
            if (arrayList.size==0){
                Toast.makeText(this, "Scan you card", Toast.LENGTH_SHORT).show()
            }else{
                for (b in 0 until arrayList.size){
                    val send = Send(0, arrayList[b]["rfid"]!!, "signout", arrayList[b]["signout_date"]!!,"", "", "", "")
                    sendViewModel.insert(send)
                    println("send $send")

                }
                finish()
            }
        }

        signoutViewModel.getAllById(storage.uSERID!!).observe(this){data->
            if(data.isNotEmpty()){
                b=0
                arrayList.clear()
                for(a in data.indices){
                    val hash = HashMap<String, String>()
                    hash["rfid"] = data[a].rfid_no
                    hash["signout_date"] = data[a].signout_date
                    hash["sent"] = data[a].sent.toString()


                    if (!data[a].sent) {
                        b+=1
                        arrayList.add(hash)
                    }



                }
                val recyclerViewCodes = RecyclerViewCodes(this, arrayList)
                val linearLayoutManager = LinearLayoutManager(this)
                binding.recycler.layoutManager = linearLayoutManager
                binding.recycler.itemAnimator = DefaultItemAnimator()
                binding.recycler.adapter = recyclerViewCodes

            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            // Extract information from the intent's extras if needed
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

            // Process the tag data as needed
            // You might want to use NFC-related classes like Ndef or IsoDep to handle specific tag types

            // For example, you can read the UID of the tag:
            val uidBytes: ByteArray? = tag?.id

            val uid = uidBytes?.joinToString(":") { byte -> String.format("%02X", byte) }
            val uidString: String = byteArrayToHexString(uidBytes!!)

            // Now 'uidString' contains the UID of the NFC tag
            // Print or use the UID as needed
            println("NFC Tag UID: $uidString")


            // Now 'uid' contains the UID of the NFC tag
            // Print the UID as a string
            println("NFC Tag UID: $uid")




            if (tag != null) {
                val mifareClassic = MifareClassic.get(tag)

                try {
                    mifareClassic?.connect()

                    if (mifareClassic != null && mifareClassic.isConnected) {
                        // Authenticate with the MIFARE Classic card
                        val sector = 0 // Sector 0
                        mifareClassic.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT)

                        // Read the content of block 0 in sector 0
                        val blockNumber = sector * 4 // Each sector has 4 blocks
                        val blockData = mifareClassic.readBlock(blockNumber)

                        // Convert blockData to a readable format
                        val dataAsString = String(blockData, Charsets.UTF_8)

                        println("Data from Sector $sector, Block $blockNumber: $dataAsString")
                        val hexString = blockData.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

                        println("Hexadecimal Data from Sector $sector, Block $blockNumber: $hexString")
                        val signout = Signout(hexString, storage.uSERID!!,ShortCut_To.getCurrentDateTime(), false)

                        signoutViewModel.insert(signout)
                    } else {
                        println("MifareClassic is null or not connected.")
                    }
                } catch (e: IOException) {
                    println("Error during MifareClassic communication: ${e.message}")
                } finally {
                    try {
                        mifareClassic?.close()
                    } catch (e: IOException) {
                        println("Error closing MifareClassic: ${e.message}")
                    }
                }
            }

            // Handle the UID or any other data you're interested in
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

            if (tag != null) {
                val isoDep = IsoDep.get(tag)

                try {
                    isoDep?.connect()

                    if (isoDep != null && isoDep.isConnected) {
                        // Authenticate with the MIFARE Classic card
                        val key = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
                        val sector = 0 // Sector 0
                        isoDep.transceive(byteArrayOf(0x1A.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
                        isoDep.transceive(byteArrayOf(0x0A.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), sector.toByte()))

                        // Read the content of block 0 in sector 0
                        val blockNumber = sector * 4 // Each sector has 4 blocks
                        val readCommand = byteArrayOf(0x30.toByte(), blockNumber.toByte())
                        val blockData = isoDep.transceive(readCommand)

                        // Convert blockData to a readable format
                        val dataAsString = String(blockData, Charsets.UTF_8)

                        println("Data from Sector $sector, Block $blockNumber: $dataAsString")
                    } else {
                        println("IsoDep is null or not connected.")
                    }
                } catch (e: IOException) {
                    println("Error during IsoDep communication: ${e.message}")
                } finally {
                    try {
                        isoDep?.close()
                    } catch (e: IOException) {
                        println("Error closing IsoDep: ${e.message}")
                    }
                }
            }
        }
    }

    private fun byteArrayToHexString(array: ByteArray): String {
        val hexChars = CharArray(array.size * 2)
        for (i in array.indices) {
            val v = array[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }

        return String(hexChars)
    }



}