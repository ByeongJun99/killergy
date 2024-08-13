package com.inu.killergy.WeeklyReport;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekAxisValueFormatter extends ValueFormatter {
    private String[] daysOfWeek = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일","일요일"};
    private ArrayList<String> thisWeek;
    
    WeekAxisValueFormatter(){
        thisWeek = new ArrayList<String>();
        LocalDate localDate = LocalDate.now();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        for (int i=dayOfWeek.getValue(); i<dayOfWeek.getValue()+7;i++){
            thisWeek.add(daysOfWeek[i%7]);
        }
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        if((int)value == -1){ return thisWeek.get(0);}
        return thisWeek.get((int) value);
    }
}