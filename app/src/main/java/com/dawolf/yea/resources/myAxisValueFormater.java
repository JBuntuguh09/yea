package com.dawolf.yea.resources;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class myAxisValueFormater implements IAxisValueFormatter {

    String[] mValues2 = new String[]{"1", "B", "C","D"};
    public myAxisValueFormater(String[] values) {
        //mValues2=values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int x;
        for( x=0; x<mValues2.length; x++) {

            String[] a = String.valueOf(value).split(".");

        }
        return mValues2[(int)(value)];

    }
}
