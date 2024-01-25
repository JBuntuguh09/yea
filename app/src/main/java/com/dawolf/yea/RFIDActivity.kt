package com.dawolf.yea

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dawolf.yea.databinding.ActivityRfidactivityBinding
import com.dawolf.yea.models.Readmode
import com.dawolf.yea.resources.Storage
import com.dawolf.yea.utils.DevBeep
import com.google.android.material.button.MaterialButton
import com.olc.uhf.UhfManager
import com.olc.uhf.tech.ISO1800_6C
import com.olc.uhf.tech.IUhfCallback

class RFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidactivityBinding
    private lateinit var storage: Storage
    var startBtn: MaterialButton? = null
    var bulkScanRecyclerview: RecyclerView? = null
     var adapter: MuiltSelAdapter? = null //listview adapter

    // private UhfManager mService;
    private val uhf_6c: ISO1800_6C? = null
    var allcount = 0

    private var isLoop = false
    private var Index = 1

    private val bst_readTid: Button? = null
    private var bst_readEpc: Button? = null
    private val list_read: ListView? = null

//    private val adapter: com.vangtech.nerawaste_customer.activities.RFIDActivity.MuiltSelAdapter? =
//        null //listview adapter

    private val readermodes = ArrayList<Readmode>()
    var mService: UhfManager? = null
    var isOpen = false
   // val TAG: String = AppController::class.java.getSimpleName()

     val mHandler: Handler = MainHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfidactivity)

       readermodes.clear()
       adapter = MuiltSelAdapter(this, readermodes)



       list_read!!.choiceMode = ListView.CHOICE_MODE_SINGLE


    }

    fun LoopReadEPC() {
        val thread = Thread {
            while (isLoop) {
                uhf_6c!!.inventory(callback)
                if (!isLoop) {
                    break
                }
                try {
                    Thread.sleep(150)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }
            }
        }
        thread.start()
    }

    fun startRFIDScan(view: View?) {
        if (!isLoop) {
            isLoop = true
            LoopReadEPC()
            binding.startBtn.setText(R.string.stop)
        } else if (isLoop) {
            isLoop = false
            binding.startBtn.setText(R.string.start)
        }
    }

    fun clearEpc(view: View?) {
       // adapter.clearEpcRecords()
    }

    //    @Override
    //    public void doInventory(List<String> list) throws RemoteException {
    //
    //
    //    }
    //
    //    @Override
    //    public void doTIDAndEPC(List<String> list) throws RemoteException {
    //
    //    }
    //
    //    @Override
    //    public IBinder asBinder() {
    //        return null;
    //    }
    private class MainHandler : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val model = Readmode()
                val readerdata = msg.obj as String
                run {
                    model.tidNo = ""
                    model.epcNo = readerdata
                  //  adapter.notifyDataSetChanged()
                }
                // m_number++;
                //IshavaCode(model, 1)
            }
        }
    }
    ;



    @SuppressLint("ResourceAsColor")
     fun IshavaCode(code: Readmode, number: Int): Boolean? {
        val count = readermodes.size
        var newcount = 0
        for (i in 0 until count) {
            if (readermodes[i].getEPCNo().equals(code.getEPCNo())) {
                newcount = readermodes[i].getCountNo().toInt()
                if (newcount >= 2000000000) { //2000000000
                    newcount = 0
                    readermodes[i].setCountNo("0")
                }
                // 4294967296
                readermodes[i].setCountNo((newcount + 1).toString())
                adapter = MuiltSelAdapter(this, readermodes)
                list_read!!.adapter = adapter
                //
                list_read.setSelection(list_read.count - 1)
                //list_read.smoothScrollToPosition(list_read.getCount() -1);//
                //list_read.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                // 18666992511
                // list_read.getSelectedView().setBackgroundColor(Color.YELLOW);
                // list_read.selector//getSelectedView().setBackgroundColor(Color.YELLOW);
                return true
            }
        }
        Index = readermodes.size + 1
        val model = Readmode()
        model.setEPCNo(code.getEPCNo())
        model.setTIDNo("" + Index++)
        model.setCountNo(number.toString())
        readermodes.add(model)
        adapter = MuiltSelAdapter(this, readermodes)
        list_read!!.adapter = adapter
        val card_num = readermodes.size
        list_read.setSelection(list_read.count - 1)
        //list_read.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        return false
    }


    //turn this to static and test
    var callback: IUhfCallback = object : IUhfCallback.Stub() {
        @Throws(RemoteException::class)
        override fun doInventory(str: List<String>) {
            // for (Iterator it2 = str.iterator(); it2.hasNext();)
            Log.e("RFIDActivity", "List of Callback String Count=" + str.size)
            Log.d("dqw", "count111=" + str.size)
            allcount += str.size
            Log.d("dqw00000007", "count111=$allcount")
            for (i in str.indices) {
                val strepc = str[i]
                Log.d("wyt", "RSSI=" + strepc.substring(0, 2))
                Log.d("wyt", "PC=" + strepc.substring(2, 6))
                Log.d("wyt", "EPC=" + strepc.substring(6))
                DevBeep.PlayOK()
                //                String strEpc = strepc.substring(2, 6) + strepc.substring(6);
                val strEpc = strepc.substring(6)
                val msg = Message()
                msg.what = 1
                msg.obj = strEpc
                mHandler.sendMessage(msg)
            }
        }

        @Throws(RemoteException::class)
        override fun doTIDAndEPC(str: List<String>) {
            val it2: Iterator<*> = str.iterator()
            while (it2.hasNext()) {
                val strepc = it2.next() as String
                // Log.d("wyt", strepc);
                val nlen = Integer.valueOf(strepc.substring(0, 2), 16)
            }
        }
    }


    class MuiltSelAdapter @SuppressLint("UseSparseArrays") constructor(
        private val context: Context,
        models: MutableList<Readmode>
    ) :
        BaseAdapter() {
        var isSelected: java.util.HashMap<Int, Boolean>
        private var inflater: LayoutInflater? = null
        private var models: MutableList<Readmode> = java.util.ArrayList<Readmode>()

        init {
            this.models = models
            inflater = LayoutInflater.from(context)
            isSelected = java.util.HashMap()
            initData(false)
        }

        fun initData(flag: Boolean) {
            for (i in models.indices) {
                isSelected[i] = flag
            }
        }

        fun clearEpcRecords() {
            models.clear()
            notifyDataSetInvalidated()
        }

        override fun getCount(): Int {
            return models.size
        }

        override fun getItem(arg0: Int): Any {
            return models[arg0]
        }

        override fun getItemId(arg0: Int): Long {
            return arg0.toLong()
        }

        override fun getView(
            position: Int, convertView: View,
            parent: ViewGroup
        ): View {
            var convertView = convertView
            var holder: ViewHolder? = null
            if (convertView == null) {
                holder = ViewHolder()
                convertView = inflater!!.inflate(R.layout.epc_scan_item, null)
                holder.tv_EPCNo = convertView
                    .findViewById<View>(R.id.epc) as TextView
                holder.tv_TIDNo = convertView
                    .findViewById<View>(R.id.no) as TextView
                holder.tv_CountNo = convertView
                    .findViewById<View>(R.id.count) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
//            val model: Readmode = readermodes.get(position)
//            holder!!.tv_EPCNo.setText(model.getEPCNo())
//            holder.tv_TIDNo.setText(model.getTIDNo())
//            holder.tv_CountNo.setText(model.getCountNo())
            return convertView
        }

        internal inner class ViewHolder {
            var tv_EPCNo: TextView? = null
            var tv_TIDNo: TextView? = null
            var tv_CountNo: TextView? = null
        }
    }


}
