package com.dawolf.yea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawolf.yea.database.send.Send;
import com.dawolf.yea.database.send.SendViewModel;
import com.dawolf.yea.databinding.ActivityRfidactivity2Binding;
import com.dawolf.yea.models.Readmode;
import com.dawolf.yea.resources.ShortCut_To;
import com.dawolf.yea.resources.Storage;
import com.dawolf.yea.utils.API;
import com.dawolf.yea.utils.AppController;

import com.dawolf.yea.utils.AppUtils;
import com.dawolf.yea.utils.DevBeep;
import com.google.android.material.button.MaterialButton;
import com.olc.uhf.UhfAdapter;
import com.olc.uhf.UhfManager;
import com.olc.uhf.tech.ISO1800_6C;
import com.olc.uhf.tech.IUhfCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RFIDActivity2 extends AppCompatActivity {

    ActivityRfidactivity2Binding binding;
    MaterialButton startBtn;
    AppUtils appUtils;
    RecyclerView bulkScanRecyclerview;

    // private UhfManager mService;
    private ISO1800_6C uhf_6c;
    int allcount = 0;

    private boolean isLoop =false;
    private int Index = 1;

    private Button bst_readTid, bst_readEpc;
    private ListView list_read;

    private MuiltSelAdapter adapter; //listview adapter
    private List<Readmode> readermodes = new ArrayList<Readmode>();
    public static UhfManager mService;
    boolean isOpen;
    public static final String TAG = AppController.class.getSimpleName();

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private LocationManager locationManager;
    private int LOCATION_PERMISSION_REQUEST_CODE = 100;
    String lats = "1.00";
    String longs ="1.00";
    private Storage storage ;

    private SendViewModel sendViewModel;



    private Handler mHandler = new MainHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRfidactivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appUtils = new AppUtils(this);
        storage = new Storage(this);
        sendViewModel =  new ViewModelProvider(this, getDefaultViewModelProviderFactory()).get(SendViewModel.class);

        if(AppController.mService!=null) {
            binding.startBtn.setEnabled(true);
            uhf_6c = (ISO1800_6C) AppController.mService.getISO1800_6C();
            DevBeep.init(RFIDActivity2.this);

        }else {
            binding.startBtn.setEnabled(false);
            Log.e("RFIDActivity", "Status of AppController.mService==> false" + AppController.getInstance().getIsOpen());
        }

        requestPermission();
        //forceStartRfid();
        startBtn = binding.startBtn;
        list_read = binding.bulkScanRecyclerview;
        readermodes.clear();
        adapter = new MuiltSelAdapter(this, readermodes);
        list_read.setAdapter(adapter);
        list_read.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter.notifyDataSetChanged();
        list_read.setOnItemClickListener(itemClickListener);


        getButtons();
    }

    private void getButtons() {
        binding.btnSubmit.setOnClickListener(view -> {
            if(readermodes.size()==0){
                Toast.makeText(this, "Scan your card", Toast.LENGTH_SHORT).show();
            }else if(Objects.equals(lats, "0.00") && Objects.equals(longs, "0.00")){
                Log.d("lats", longs+"//"+lats);
                requestPermission();
            }else {
                if(Objects.equals(storage.getProject(), "Attendance")){
                    ArrayList<String> list = new ArrayList<>();

                    for (int a=0; a<readermodes.size(); a++){
                        //sendData
                        if(!list.contains(readermodes.get(a).getEPCNo())) {
                            String regId = storage.getRegionId();
                            String distId = storage.getDistrictId();
                            if(Objects.equals(storage.getRegionId(), "null")) {
                               regId = "01b28b8d-8bfa-44b6-925b-ea1c27ccf5de";
                            }
                            if(Objects.equals(storage.getDistrictId(), "null")) {
                                distId = "171bcd54-93ec-4286-8b7a-5d9f6808a810";
                            }
                            assert distId != null;
                            assert regId != null;

                            Send send = new Send(0, readermodes.get(a).getEPCNo(), "attendance", "", regId, distId,
                                    lats, longs, "Unsent");
                            sendViewModel.insert(send);
                            list.add(readermodes.get(a).getEPCNo());
                        }
                    }
                    Toast.makeText(this, "Sending Sign Ins", Toast.LENGTH_SHORT).show();
                    finish();
                }else if(Objects.equals(storage.getProject(), "Signout")){
                    ArrayList<String> list = new ArrayList<>();

                    for (int a=0; a<readermodes.size(); a++){
                        //sendData
                        if(!list.contains(readermodes.get(a).getEPCNo())){
                            Send send = new Send(0, readermodes.get(a).getEPCNo(), "signout", ShortCut_To.INSTANCE.getCurrentDateTime(),"", "",
                                    lats, longs, "Unsent");
                            sendViewModel.insert(send);
                            list.add(readermodes.get(a).getEPCNo());
                        }
                    }
                    Toast.makeText(this, "Sending Sign Ins", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        });
    }



    void forceStartRfid(){


        try {
            mService = UhfAdapter.getUhfManager(this);
            isOpen = mService.open();

            if(mService!=null) {
                Log.e(TAG, "RFIDActivity onCreate: mService!=null==>" );

                uhf_6c = (ISO1800_6C) AppController.mService.getISO1800_6C();
                DevBeep.init(RFIDActivity2.this);

            }

            Log.e(TAG, "RFIDActivity onCreate: isOpen==>" + isOpen );
            Log.e(TAG, "RFIDActivity onCreate: Status==>" + mService.getStatus());
            Log.e(TAG, "AppController onCreate: StatusDesc ==>" + mService.getStatusDescribe());


        } catch (Exception e) {
            Log.e(TAG, "RFIDActivity onCreate: Error " + e.toString() );
            //Toast.makeText(AppController.this, "dasd", Toast.LENGTH_SHORT).show();
        }

    }

    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView < ? > listView, View view, int position, long id) {

//            String info = ( (TextView) arg1 ).getText().toString();
            final Readmode epc = (Readmode) adapter.getItem(position);

            if(storage.getProject()=="Register"){
                Toast.makeText( getBaseContext(), "Item: " + epc.getEPCNo()+ " selected", Toast.LENGTH_LONG ).show();
                storage.setRandVal(epc.getEPCNo());
                finish();
            }else {
                Toast.makeText( getBaseContext(), "Item: " + epc.getEPCNo(), Toast.LENGTH_LONG ).show();

            }


        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isLoop=false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(Objects.equals(storage.getProject(), "Attendance")){
            binding.txtTopic.setText("SIGN IN: Scan your card");
        }else if(Objects.equals(storage.getProject(), "Signout")){
            binding.txtTopic.setText("SIGNOUT : Scan your card");
        }else if(Objects.equals(storage.getProject(), "Scan your card")){
            binding.txtTopic.setText("Scan your card");
        }
//        if(mService == null){
//            forceStartRfid();
//        }
        /** Since it appears appController rfid service may not be open in activity, lets call
         * it to reopen the scanning function.
         * Fingers Crossed this works all the time. Update--- It works all the time. Yaaaasss!!!
         * **/
        try{
            AppController.mService.open();

            if(startBtn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.stop)))
            {
                isLoop=true;
                LoopReadEPC();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoop = false;
        try {
            AppController.mService.close();

            Log.e("RFIDActivity.OnD", "Status of AppController.mService==> " + AppController.getInstance().getIsOpen());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void LoopReadEPC() {

        try {
            Thread thread = new Thread(() -> {
                while (isLoop) {
                    uhf_6c.inventory(callback);
                    if (!isLoop) {
                        break;
                    }
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;

                    }
                }
            });
            thread.start();
        }catch (Exception e){
            Toast.makeText(this, "Sorry. Your device does not support this feature", Toast.LENGTH_SHORT).show();
        }
    }

    public void startRFIDScan(View view) {
        if (!isLoop) {
            isLoop = true;
            LoopReadEPC();
            startBtn.setText(R.string.stop);
        }

        else if (isLoop) {
            isLoop = false;
            startBtn.setText(R.string.start);
        }
    }

    public void clearEpc(View view) {
        adapter.clearEpcRecords();
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


    private class MainHandler extends Handler {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Readmode model = new Readmode();
                String readerdata = (String) msg.obj;
                {
                    model.setTIDNo("");
                    model.setEPCNo(readerdata);
                    adapter.notifyDataSetChanged();
                }
                // m_number++;
                IshavaCode(model, 1);
            }

        }
    };

    @SuppressLint("ResourceAsColor")
    private Boolean IshavaCode(Readmode code, int number) {
        int count = readermodes.size();
        int newcount = 0;
        for (int i = 0; i < count; i++) {
            if (readermodes.get(i).getEPCNo().equals(code.getEPCNo())) {

                newcount = Integer.parseInt(readermodes.get(i).getCountNo());
                if(newcount>=2000000000){//2000000000
                    newcount=0;
                    readermodes.get(i).setCountNo("0");
                }
                // 4294967296
                readermodes.get(i).setCountNo(String.valueOf((newcount+1)));
                adapter = new MuiltSelAdapter(this, readermodes);
                list_read.setAdapter(adapter);
                //
                list_read.setSelection(list_read.getCount()-1);
                //list_read.smoothScrollToPosition(list_read.getCount() -1);//
                //list_read.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                // 18666992511
                // list_read.getSelectedView().setBackgroundColor(Color.YELLOW);
                // list_read.selector//getSelectedView().setBackgroundColor(Color.YELLOW);
                return true;
            }
        }
        Index = readermodes.size()+1;
        Readmode model = new Readmode();
        model.setEPCNo(code.getEPCNo());
        model.setTIDNo("" + Index++);
        model.setCountNo(String.valueOf(number));
        readermodes.add(model);
        adapter = new MuiltSelAdapter(this, readermodes);
        list_read.setAdapter(adapter);
        int card_num = readermodes.size();
        list_read.setSelection(list_read.getCount()-1);
        //list_read.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        if(Objects.equals(storage.getProject(), "Register")){
            Toast.makeText( getBaseContext(), code.getEPCNo()+" selected", Toast.LENGTH_LONG ).show();
            storage.setRandVal(code.getEPCNo());
            finish();
        }
        return false;
    }


    //turn this to static and test
    IUhfCallback callback = new IUhfCallback.Stub() {
        @Override
        public void doInventory(List<String> str) throws RemoteException {
            // for (Iterator it2 = str.iterator(); it2.hasNext();)
            Log.e("RFIDActivity", "List of Callback String Count=" + str.size());

            Log.d("dqw", "count111=" + str.size());
            allcount += str.size();
            Log.d("dqw00000007", "count111=" + allcount);

            for (int i = 0; i < str.size(); i++) {
                String strepc = (String) str.get(i);
                Log.d("wyt", "RSSI=" + strepc.substring(0, 2));
                Log.d("wyt", "PC=" + strepc.substring(2, 6));
                Log.d("wyt", "EPC=" + strepc.substring(6));
                DevBeep.PlayOK();
//                String strEpc = strepc.substring(2, 6) + strepc.substring(6);
                String strEpc = strepc.substring(6);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = strEpc;
                mHandler.sendMessage(msg);
            }
        }

        @Override
        public void doTIDAndEPC(List<String> str) throws RemoteException {
            for (Iterator it2 = str.iterator(); it2.hasNext();) {
                String strepc = (String) it2.next();
                // Log.d("wyt", strepc);
                int nlen = Integer.valueOf(strepc.substring(0, 2), 16);
                // Log.d("wyt", "PC=" + strepc.substring(2, 6));
                // Log.d("wyt", "EPC=" + strepc.substring(6, (nlen + 1) * 2));
                // Log.d("wyt", "TID=" + strepc.substring((nlen + 1) * 2));

            }
        }

    };

    class MuiltSelAdapter extends BaseAdapter {
        private Context context;
        private HashMap<Integer, Boolean> isSelected;
        private LayoutInflater inflater = null;
        private List<Readmode> models = new ArrayList<Readmode>();

        @SuppressLint("UseSparseArrays")
        public MuiltSelAdapter(Context context, List<Readmode> models) {
            this.context = context;
            this.models = models;
            inflater = LayoutInflater.from(context);
            isSelected = new HashMap<Integer, Boolean>();
            initData(false);
        }

        public void initData(boolean flag) {
            for (int i = 0; i < models.size(); i++) {
                isSelected.put(i, flag);
            }
        }

        public void clearEpcRecords() {
            models.clear();
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public Object getItem(int arg0) {
            return models.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.epc_scan_item, null);
                holder.tv_EPCNo = (TextView) convertView
                        .findViewById(R.id.epc);
                holder.tv_TIDNo = (TextView) convertView
                        .findViewById(R.id.no);
                holder.tv_CountNo = (TextView) convertView
                        .findViewById(R.id.count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Readmode model = readermodes.get(position);
            holder.tv_EPCNo.setText(model.getEPCNo());
            holder.tv_TIDNo.setText(model.getTIDNo());
            holder.tv_CountNo.setText(model.getCountNo());
            return convertView;
        }

        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
            this.isSelected = isSelected;
        }

        class ViewHolder {
            TextView tv_EPCNo;
            TextView tv_TIDNo;
            TextView tv_CountNo;

        }
    }


    //permission

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d("wassssop", "ffffffffffNooo");
        }else {
            Log.d("wassssop", "ffffffffff");
            getLocation();
        }
    }
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Log.d("wassssop", latitude+"///"+latitude);
                lats = String.valueOf(latitude);
                longs = String.valueOf(longitude);

                Log.d("wassssopdd", lats+"///"+longs);


                // Now you have the latitude and longitude, you can use them as needed
                // For example, display in a TextView or send to a server
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("wassssop", "granted");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start getting the location
                getLocation();
            } else {
                Toast.makeText(this, "Allow location permissions", Toast.LENGTH_SHORT).show();
                // Permission denied, handle accordingly (e.g., show a message or disable location features)
            }
        }
    }




    }