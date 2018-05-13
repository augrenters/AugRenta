package com.example.sejeque.augrenta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SejeQue on 5/10/2018.
 */

public class CalendarRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "DatePickerDialog");




        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DAY_OF_MONTH, 3000);

        List<Calendar> dayslist= new LinkedList<Calendar>();
        Calendar[] daysArray;

        while ( calendar.getTimeInMillis() <= gc.getTimeInMillis()) {
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(calendar.getTimeInMillis());
                Log.d("Selected date", ""+ calendar.getTimeInMillis());

                dayslist.add(c);
            }
            calendar.setTimeInMillis(calendar.getTimeInMillis() + (24*60*60*1000));
        }
        daysArray = new Calendar[dayslist.size()];
        for (int i = 0; i<daysArray.length;i++) {
            daysArray[i]=dayslist.get(i);
        }
        dpd.setSelectableDays(daysArray);
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d("Selected date", date);
    }
}
