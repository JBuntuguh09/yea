package com.dawolf.yea.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.olc.uhf.UhfAdapter;
import com.olc.uhf.UhfManager;
import com.orhanobut.hawk.Hawk;


import org.json.JSONObject;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class AppController extends Application {

    public String path;


// New Reader params
    public static UhfManager mService;
    public static String mversion="";
    public static int  m_version=2;
    boolean isOpen;

    public static final String TAG = AppController.class.getSimpleName();

    private AppUtils appUtils;
    private static AppController mInstance;
    Context mContext;



    @Override
    public void onCreate() {
        Log.d("lllllll", "moooooooooooooooo");
        super.onCreate();
        mInstance = this;
        mContext = this.getApplicationContext();
        appUtils = new AppUtils(mInstance);
        Hawk.init(mContext).build();

        try {
            mService = UhfAdapter.getUhfManager(this.mContext);
            isOpen = mService.open();

            Log.e(TAG, "AppController onCreate: UhfAdapter isOpen==>" + isOpen );
            Log.e(TAG, "AppController onCreate: UhfAdapter Status==>" + mService.getStatus());
            Log.e(TAG, "AppController onCreate: UhfAdapter StatusDesc ==>" + mService.getStatusDescribe());

        } catch (Exception e) {
            Log.e(TAG, "AppController onCreate: UhfAdapter Error " + e );
        }

        //AndroidNetworking.initialize(getApplicationContext(), getUnsafeOkHttpClient());
        AndroidNetworking.initialize(getApplicationContext());
        initAppData(appUtils.getPref(AppConstants.AGENT_CODE, "AGT971821")); //use constant user data created for Docupro agent

        //new device adapter.
//        try {
//            mService = UhfAdapter.getUhfManager(this.getApplicationContext());
//            if (mService != null) {
//                int status=mService.getStatus();
//                m_version=((status&0xff0000)>>16)&0xff;
//                if(mService.open()){
//                    int b=0;
//                }
//            }
//        } catch (Exception e) {
//            //Toast.makeText(AppController.this, "dasd", Toast.LENGTH_SHORT).show();
//        }



    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public boolean getIsOpen() {
        return isOpen;
    }

    public void initAppData(String agentUserCode) {
        System.out.println("++++++++++++++++====  AppController.initAppData() Initiated  ====++++++++++++++++++" + appUtils.getPref(AppConstants.AGENT_PROVIDER_CODE, "10"));

//        if (appUtils.isNetworkAvailable()) {
//            AndroidNetworking.get(EndPoints.PROVIDER_DROPDOWN_DATA)
//                    .addHeaders("Authorization", "Bearer " + Hawk.get(AppConstants.API_TOKEN))
//                    .setPriority(Priority.LOW)
//                    .build()
//                    .getAsJSONObject(new JSONObjectRequestListener() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            System.out.println("App controller DATA OBTAINED--->" + response.toString());
//
//                            if (response.optString("code").equals(AppConstants.SUCCESS_REQUEST_CODE)){
//                                if (response.has("data")) {
//                                    String appData = response.optString("data");
//                                    Log.d(TAG, "onResponse initAppData:>>>>>>>>>> " + appData);
//
//                                    appUtils.setPref(AppConstants.PROVIDER_DATA_KEY, response.optString("data"));
////                                DatabaseHelper dbhelper = new DatabaseHelper(AppController.this);
////                                dbhelper.insertAppData(AppConstants.APP_DATA_DROP_DOWNS,appData);
//                                }
//                            }else{
//                                Log.e(TAG, "Parse Error: " + response);
//
//                            }
//
//                        }
//                        @Override
//                        public void onError(ANError error) {
//                            // handle error
//                            Log.e(TAG, "onError: " + error.getErrorDetail());
//                        }
//                    });
//
//        } else {
//            System.out.println("@@@@@@@@@@@@@@@ NO INTERNET");
//        }
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

