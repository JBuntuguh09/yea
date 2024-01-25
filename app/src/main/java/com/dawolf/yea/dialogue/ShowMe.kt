package com.dawolf.yea.dialogue

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.dawolf.yea.MainBase
import com.dawolf.yea.R
import com.dawolf.yea.Startpage
import com.dawolf.yea.database.attendance.Attendance
import com.dawolf.yea.database.attendance.AttendanceViewModel
import com.dawolf.yea.database.signout.Signout
import com.dawolf.yea.database.signout.SignoutViewModel
import com.dawolf.yea.databinding.LayoutScanOptionsBinding
import com.dawolf.yea.fragments.signout.AttendanceSignout
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage

object ShowMe {
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var storage: Storage

    fun ScanOptions(context: Context, linearLayout: LinearLayout, hexString: String,
                    attendanceViewModel: AttendanceViewModel, signoutViewModel: SignoutViewModel){
        val layoutInflater = LayoutInflater.from(context)
        alertDialog = AlertDialog.Builder(context)
        storage = Storage(context)


        val view = layoutInflater.inflate(R.layout.layout_scan_options, linearLayout, false)
        val binding = LayoutScanOptionsBinding.bind(view)
        binding.cardAttendance.setOnClickListener {
            dialog.dismiss()
            val attendance = Attendance( hexString, storage.uSERID!!,  storage.regionId!!, storage.districtId!!,
                (context as MainBase).lat, (context as MainBase).long,
                ShortCut_To.currentDatewithTime,  false)
            attendanceViewModel.insert(attendance)

            val intent = Intent(context, Startpage::class.java)
            context.startActivity(intent)
        }
        binding.cardSignout.setOnClickListener {
            val signout = Signout(hexString, storage.uSERID!!,ShortCut_To.getCurrentDateTime(), false)
            signoutViewModel.insert(signout)
            dialog.dismiss()
            val intent = Intent(context, AttendanceSignout::class.java)
            context.startActivity(intent)
        }

       alertDialog.setView(view)
        dialog = alertDialog.create()
        dialog.show()


    }
}