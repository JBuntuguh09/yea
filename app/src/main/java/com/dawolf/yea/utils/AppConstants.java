package com.dawolf.yea.utils;


public class AppConstants {


    public static final int POST_STATUS_CODE = 201;
    public static final int GET_STATUS_CODE = 200;
    public static final int PUT_DELETE_STATUS_CODE = 204;
    public static final String DATA_KEY = "data";
    public static final String KEY_CUSTOMERS = "customers";
    public static final String KEY_CUSTOMER_ID = "customerId";
    public static final String SUCCESS_REQUEST_CODE = "00";

    public static final String REGEX_PHONE = "\\d{10}";
    public static final String REGEX_NAME = "[a-zA-Z\\s]+";
    public static final String TAG_JSON_OBJECT_REQUEST = "JSON_OBJECT_REQUEST";
    public static final String TAG_JSON_ARRAY_REQUEST = "JSON_ARRAY_REQUEST";
    public static final int GEOPOINT_REQUEST_CODE = 101;
    public static final int RFID_REQUEST_CODE = 200;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";



    public static final String OPTION_YES = "Yes";
    public static final String OPTION_NO = "No";
    public static final String RFID_TAG_NUMBER = "tagNumber";


    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String API_TOKEN = "access_token";
    public static final String AGENT_ID = "agentId";
    public static final String AGENT_NAME = "agentName";

    public static final String AGENT_LASTNAME = "agentLastName";
    public static final String AGENT_MIDDLENAME = "agentMiddleName";
    public static final String AGENT_CODE = "agentCode";
    public static final String AGENT_TELEPHONE = "agentTelephone";
    public static final String AGENT_BIRTHDATE = "birthDate";
    public static final String HASLOGIN = "hasLogin";
    public static final String IS_FIRST_LOGIN = "isFirstLogin";
    public static final String AGENT_PROVIDER_CODE = "spCode";
    public static final String PROVIDER_DATA_KEY = "providerData";


    // Password Change Activity
    public static final String ACTIVITY_SETUP_CODE = "activitySetupCode";
    public static final int FIRST_PASSWORD_CHANGE = 40; //this happens on first time login.
    public static final int NORMAL_PASSWORD_CHANGE = 41;





    public static final int APP_DATA_DROP_DOWNS = 110;

    //DROP DOWN COLUMN JSON KEYS


    public static final String JS_KEY_REGIONS = "regions";
    public static final String JS_KEY_MMDAS = "mmdas";
    public static final String JS_KEY_SUB_METROS = "sub_metros";
    public static final String JS_KEY_COMMUNITIES = "communities";
    public static final String JS_KEY_BIN_TYPES = "bin_types";
    public static final String JS_KEY_CUSTOMER_TYPES = "customer_types";
    public static final String JS_KEY_SERVICE_PROVIDERS = "service_providers";
    public static final String JS_KEY_SERVICE_PROVIDER = "service_provider";
    public static final String JS_KEY_CUSTOMER_STATUS = "customerStatus";
    public static final String JS_KEY_REVENUE_COLLECTOR = "revenueCollector";

    //public static final IDType ID_TYPE_LIST = new IDType[]{new IDType("GH", "Ghana Card")}



    //2018-05-24T12:40:22.950013
    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static final String DATE_FORMAT_LOCAL = "yyyy-MM-dd";


    // ContentProvider information
    public static final String CONTENT_AUTHORITY = "com.melcbin.sync";
    public static final long SYNC_FREQUENCY = 60 * 15; // 1 hour (seconds)

    public static final String SYNC_KEY_BINS = "bins";
    public static final String SYNC_KEY_CUSTOMERS = "customers";
    public static final String SYNC_KEY_PICKED_BINS = "pickedBins";

    public static final String SEARCH_TYPE_CUST_ID = "customer_id";
    public static final String SEARCH_TYPE_PHONE = "primary_phone";
    public static final String SEARCH_TYPE_NAME = "customer_name";

    public static final String SEARCH_TYPE_BIN_CODE = "binCode";


    public static final int BIN_240_LTR_ID = 2;
    public static final int BIN_60_LTR_ID = 67;
    public static final int BIN_120_LTR_ID = 1;

    public static final int DB_INSERT_ID = 101;

    public static final String VEHICLE_ID="vehicleId";
    public static final int CUSTOMER_ID_LENGTH=8;


}
