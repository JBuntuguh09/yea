package com.dawolf.yea.utils;

public class EndPoints {
    public static final String API_TOKEN="A4ClQsW4ndM5R5La";
    //public static final String API_BASE_URL = "http://10.10.10.17:81/iwms/api";//"https://www.melchia.com/iwms/api";//"192.168.8.100:81/iwms/api";//
//    public static final String API_BASE_URL = "https://www.melchia.com/iwms/api";
    // test base url
    public static final String API_BASE_URL = "http://tosolback.nerasolgh.com:8081/api/v1";

    // Live Base Url
//    public static final String API_BASE_URL = "https://iwmis.melchia.com/api";

    //Auth
    public static final  String AUTH_USER= API_BASE_URL+"/auth/login";


    //    public static final String ALL_DROPDOWN_VALUES = EndPoints.API_BASE_URL + "/dropdowns";
    //public static final String PROVIDER_DROPDOWN_VALUES = EndPoints.API_BASE_URL + "/provider/dropdowns/"; //service provider key returns json object
    public static final String PROVIDER_DROPDOWN_DATA = EndPoints.API_BASE_URL + "/dropdowns"; //service provider key returns json array


    public static final String SAVE_BIN_DATA = EndPoints.API_BASE_URL + "/bins/distribute";
    public static final String SEARCH_CUSTOMER = EndPoints.API_BASE_URL + "/customers/search_customers_by/";
    public static final String SEARCH_CUSTOMER_RFID = EndPoints.API_BASE_URL + "/customer/search/";

    public static final String SAVE_CUSTOMER= EndPoints.API_BASE_URL + "/customers";

    public static final  String UPDATE_PASSWORD=EndPoints.API_BASE_URL+"/auth/change_password";


    public static final  String SYNC_DATA=EndPoints.API_BASE_URL+"/sync";

    public  static final String SAVE_BIN_PIC_DATA=EndPoints.API_BASE_URL+"/bins/picking";
    public static final String BIN_TRANSFER=EndPoints.API_BASE_URL+"/bins/transfer";

}
