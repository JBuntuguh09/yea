package com.dawolf.yea

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rscja.deviceapi.RFIDWithUHFA8
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.UhfBase
import java.nio.charset.Charset

class RFIDActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var uhfReader: UhfBase
    private lateinit var rfid: RFIDWithUHFUART
    private var isInventory = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfidactivity)

        // Initialize NFC adapter

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            // NFC is not supported on this device
            // Handle accordingly
        }
        uhf()

    }

    private fun uhf() {
        rfid = RFIDWithUHFUART.getInstance()
        val result = rfid.init(this)

        if (!result) {
            println("monkey")
            Toast.makeText(this, "Conexión fallida", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_SHORT).show()

        // Configuración de parámetros
        // rfid.setFrequencyMode(xx)
        // rfid.setPower(30)

        if (rfid.startInventoryTag()) {
            Toast.makeText(this, "Lectura iniciada", Toast.LENGTH_SHORT).show()
            isInventory = true
            ThreadInventory().start()
        } else {
            rfid.stopInventory()
            Toast.makeText(this, "Error al iniciar la lectura", Toast.LENGTH_SHORT).show()
        }

        // ..............

        rfid.stopInventory()
        isInventory = false

        // .................

        // Desconectar UHF
        rfid.free()
        Toast.makeText(this, "UHF desconectado", Toast.LENGTH_SHORT).show()
    }

    private inner class ThreadInventory : Thread() {
        override fun run() {
            while (isInventory) {
                val uhftagInfo = rfid.readTagFromBuffer()
                if (uhftagInfo == null) {
                    Thread.sleep(20)
                    continue
                }

                val epc = uhftagInfo.epc
                val rssi = uhftagInfo.rssi

                // .....
            }
        }
    }
}
