package com.dawolf.yea.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;

import com.androidnetworking.error.ANError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppUtils {

    private static Context _mContext;
    private static SharedPreferences mSharedPreferences;
    private static ProgressDialog pDialog;
    WorkManager mWorkManager;

    static Dialog createDialog;
    LayoutInflater layoutInflater;



    public AppUtils(Context _mContext) {
        this._mContext = _mContext;
        mSharedPreferences = this._mContext.getSharedPreferences("MyPref", 0);
        mWorkManager = WorkManager.getInstance(_mContext);


    }

    public float getDistancBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);

        return distance[0];
    }


    public float calculateDistanceTo(Location fromLocation, Location toLocation) {
        return fromLocation.distanceTo(toLocation);
    }


    public void setPref(String title, boolean value) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(title, value);
        ed.commit();

    }


    public void setPref(String title, String value) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(title, value);
        ed.commit();

    }

    public void setPref(String title, int value) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putInt(title, value);
        ed.commit();

    }

    public boolean getPref(String title, boolean def) {
        return mSharedPreferences.getBoolean(title, def);
    }


    public String getPref(String title, String def) {
        return mSharedPreferences.getString(title, def);
    }

    public int getPref(String title, int def) {
        return mSharedPreferences.getInt(title, def);
    }

    public void removePref(String key) {
        if (mSharedPreferences.contains(key)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public void showMessage(String message) {
        Toast.makeText(_mContext, message, Toast.LENGTH_LONG).show();
    }


    public String getText(EditText ettext) {
        return ettext.getText().toString();
    }



    public ArrayList<String> getDropDownItems(JSONObject json, String itemListName, String key) {
        try {
            ArrayList<String> itemsList = new ArrayList<>();
            itemsList.add("---Select " + itemListName + " ---");
            JSONArray items = json.getJSONArray(itemListName);
            for (int i = 0; i < items.length(); i++) {
                String item = items.getJSONObject(i).get(key).toString();
                itemsList.add(item);
            }
            return itemsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getJsonString(JSONObject json, String title) {
        try {
            return json.getString(title);
        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }


    public int getInt(JSONObject json, String title) {
        try {
            return json.getInt(title);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    public JSONArray getArray(JSONObject json, String title) {
        try {
            return json.getJSONArray(title);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public void clearPref() {


        setPref(AppConstants.USER_EMAIL, "");

        setPref(AppConstants.USER_EMAIL, "");
        setPref(AppConstants.USER_PASSWORD, "");


    }


    public static boolean isEmpty(String data) {
        if (data.contentEquals(""))
            return true;
        else
            return false;
    }

    public boolean validateFields(String data[], String titles[]) {
        for (int i = 0; i < data.length; i++) {
            if (isEmpty(data[i])) {
                showMessage(titles[i] + " Cannot Be Empty");
                return false;
            }
        }
        return true;
    }

    public int getSelectPos(String title, String[] titleArray) {
        int pos = 0;
        if (title.contentEquals("")) {
            return pos;
        } else {

            for (int i = 0; i < titleArray.length; i++) {
                System.out.println("region here: " + title + "=> ch: " + titleArray[i]);
                if (title.toLowerCase().contentEquals(titleArray[i].toLowerCase())) {
                    return i;
                }
            }
        }


        return pos;
    }


    public void selectSpinnerValue(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@@@ the index is" + index);
        spinner.setSelection(index);
    }


    public ArrayList<String> getSpinnerData(HashMap<String, String> items, String title) {
        ArrayList<String> itemsList = new ArrayList<>();
        itemsList.add("---Select " + title + " ---");
        itemsList.addAll(items.keySet());
//        for (String key: items.keySet()) {
//           itemsList.add(items.get(key));
//        }
        return itemsList;
    }


    public void playSound() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150);
    }

    public static void onRequest(String msg) {
        pDialog = new ProgressDialog(_mContext);
        pDialog.setMessage(msg);
        pDialog.setCancelable(false);
    }

    public static void onRequestComplete() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public int parseIntVal(String s) {
        try {
            if (s.trim().isEmpty()) {
                return 0;
            }
            return Integer.parseInt(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public Double parseDoubleVal(String s) {
        try {
            if (s.trim().isEmpty() || s.trim().equalsIgnoreCase("null")) {
                return 0.00;
            }
            return Double.parseDouble(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.00;
        }
    }

    public boolean validateField(EditText editText, String errorMsg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(errorMsg);
            editText.requestFocus();
            return false;
        }
        editText.setError(null);
        return true;

    }

    public boolean validateSpinner(Spinner spinner, String errorMsg) {
        if (spinner.getSelectedItemPosition() <= 0) {
            spinner.requestFocus();
            spinner.setFocusable(true);
            TextView textViewError = (TextView) spinner.getSelectedView();
            textViewError.setError(errorMsg);
            textViewError.setTextColor(Color.RED);
            return false;
        }
        TextView textViewError = (TextView) spinner.getSelectedView();
        textViewError.setError(null);
        textViewError.setTextColor(Color.BLACK);
        return true;
    }

//    public void setSpinnerError(MultiSpinnerSearch spinner, String errorMsg) {
//        spinner.requestFocus();
//        spinner.setFocusable(true);
//        TextView textViewError = (TextView) spinner.getSelectedView();
//        textViewError.setError(errorMsg);
//        textViewError.setTextColor(Color.RED);
//    }

    public void showAlertMsg(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(_mContext).create();//new AlertDialog.Builder(new ContextThemeWrapper(_mcontext,
        //  android.R.style.Theme_DeviceDefault_Light_Dialog));
        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        try {
            // Showing Alert Message
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        } catch (Exception ex) {
            alertDialog.dismiss();
        }
    }

    public void showAlertMsg(int resourceId) {

        AlertDialog alertDialog = new AlertDialog.Builder(_mContext).create();//new AlertDialog.Builder(new ContextThemeWrapper(_mcontext,
        //  android.R.style.Theme_DeviceDefault_Light_Dialog));
        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        // Setting Dialog Message
        alertDialog.setMessage(_mContext.getString(resourceId));

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        try {
            // Showing Alert Message
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        } catch (Exception ex) {
            alertDialog.dismiss();
        }
    }

    public void processResponse(JSONObject response) {
        try {
            if (response.getInt("code") == AppConstants.POST_STATUS_CODE) {
                String message = response.getString("message");
                showMessage(message);
            } else {
                showMessage("An Error Occurred Whiles Saving The Farmer Information");
            }
        } catch (Exception ex) {

        }
    }

    public String toSQlDate(String birthDate) {
        if (!birthDate.trim().isEmpty()) {
            try {
                String trimDate = birthDate.trim();
                String[] parts = trimDate.split("/");
                String day = parts[0];
                String month = parts[1];
                String year = parts[2];
                if (day.trim().length() < 2) {
                    day = "0" + day;
                }
                if (month.trim().length() < 2) {
                    month = "0" + month;
                }
                return year + "-" + month + "-" + day;
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return "";
    }


    public void resetForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).getText().clear();
            }
            if (view instanceof RadioGroup) {
                ((RadioButton) ((RadioGroup) view).getChildAt(0)).setChecked(true);
            }

            if (view instanceof Spinner) {
                ((Spinner) view).setSelection(0);
            }
//            if (view instanceof MultiSpinnerSearch) {
//                ((MultiSpinnerSearch) view).setSelection(-1);
//                ((MultiSpinnerSearch) view).getSelectedItems().clear();
//            }

            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(null);
            }
            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(false);
            }
            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                resetForm((ViewGroup) view);
        }
    }


    //private method of your class
    public int getSpinnerIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    public int getDistrictSpinnerIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().trim().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    public String formatDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT_SERVER);
            Date parsedDate = sdf.parse(date);
            return new SimpleDateFormat(AppConstants.DATE_FORMAT_LOCAL).format(parsedDate);

        } catch (Exception ex) {
            //ex.printStackTrace();
            String[] split = date.split("T");
            if (split.length > 0) {
                return split[0];
            }
            return "0000-00-00";
        }
    }

    public Bitmap convertImageViewToBitmap(ImageView v) {

        Bitmap bm = ((BitmapDrawable) v.getDrawable()).getBitmap();

        return bm;
    }


    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return null;

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }


}
