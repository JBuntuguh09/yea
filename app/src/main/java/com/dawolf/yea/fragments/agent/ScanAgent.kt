package com.dawolf.yea.fragments.agent

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dawolf.yea.R
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.databinding.ActivityScanAgentBinding
import com.dawolf.yea.databinding.FragmentAgentScanBinding
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import com.squareup.picasso.Picasso
import java.io.IOException

class ScanAgent : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private lateinit var attendanceViewModel: AttendanceViewModel
    private lateinit var binding: ActivityScanAgentBinding
    private lateinit var storage: Storage
    val arrayList = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Picasso.get().load(R.drawable.smartphone).into(binding.imgPic)
        Glide.with(this).asGif().load(R.drawable.smartphone).into(binding.imgPic)
        storage = Storage(this)
        storage.randVal = ""
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            // NFC is not supported on this device
            // Handle accordingly
        } else {
            pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )
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
            val uidBytes: ByteArray? = tag?.id

            val uid = uidBytes?.joinToString(":") { byte -> String.format("%02X", byte) }
            val uidString: String = byteArrayToHexString(uidBytes!!)


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
                        storage.randVal = hexString
                        finish()
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