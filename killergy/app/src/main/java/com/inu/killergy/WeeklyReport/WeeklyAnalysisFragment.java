package com.inu.killergy.WeeklyReport;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inu.killergy.DB.DailyInfo;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.R;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WeeklyAnalysisFragment extends Fragment {
    View v;
    MainDB database;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_weekly_analysis, container, false);

        LineChart lineChart1 = v.findViewById(R.id.line_chart1);
        initLineChart(lineChart1, "kcal");;

        ChipGroup chipGroup = v.findViewById(R.id.ingredientGroup);

        Chip chip_22 = v.findViewById(R.id.chip22); // id에 해당하는 chip 가져오기
        Chip chip_23 = v.findViewById(R.id.chip23);
        Chip chip_24 = v.findViewById(R.id.chip24);
        Chip chip_25 = v.findViewById(R.id.chip25);
        Chip chip_26 = v.findViewById(R.id.chip26);
        Chip chip_27 = v.findViewById(R.id.chip27);
        Chip chip_28 = v.findViewById(R.id.chip28);
        Chip chip_29 = v.findViewById(R.id.chip29);
        Chip chip_30 = v.findViewById(R.id.chip30);

        Chip chip = v.findViewById(R.id.chip22); // id에 해당하는 chip 가져오기

        LineChart lineChart2 = v.findViewById(R.id.line_chart2);
        chip_22.setTextColor(getResources().getColor(R.color.white));
        initLineChart(lineChart2,"sodium");   // 나트륨

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                Chip checkedChip = chipGroup.findViewById(checkedId);
                Resources res = getResources();
                // 문자열 리소스 ID를 가져옴.
                int chipId22 = res.getIdentifier("chip22", "id", getActivity().getPackageName());
                int chipId23 = res.getIdentifier("chip23", "id", getActivity().getPackageName());
                int chipId24 = res.getIdentifier("chip24", "id", getActivity().getPackageName());
                int chipId25 = res.getIdentifier("chip25", "id", getActivity().getPackageName());
                int chipId26 = res.getIdentifier("chip26", "id", getActivity().getPackageName());
                int chipId27 = res.getIdentifier("chip27", "id", getActivity().getPackageName());
                int chipId28 = res.getIdentifier("chip28", "id", getActivity().getPackageName());
                int chipId29 = res.getIdentifier("chip29", "id", getActivity().getPackageName());
                int chipId30 = res.getIdentifier("chip30", "id", getActivity().getPackageName());

                chip_22.setTextColor(getResources().getColor(R.color.black)); // 글자 색 바꾸기
                chip_23.setTextColor(getResources().getColor(R.color.black));
                chip_24.setTextColor(getResources().getColor(R.color.black));
                chip_25.setTextColor(getResources().getColor(R.color.black));
                chip_26.setTextColor(getResources().getColor(R.color.black));
                chip_27.setTextColor(getResources().getColor(R.color.black));
                chip_28.setTextColor(getResources().getColor(R.color.black));
                chip_29.setTextColor(getResources().getColor(R.color.black));
                chip_30.setTextColor(getResources().getColor(R.color.black));

                if (checkedChip != null) {
                    LineChart lineChart2 = v.findViewById(R.id.line_chart2);

                    if (checkedId == chipId22){
                        initLineChart(lineChart2,"sodium");
                        chip_22.setTextColor(getResources().getColor(R.color.white));
                    }

                    else if (checkedId == chipId23) {
                        initLineChart(lineChart2, "carbonhydrate");
                        chip_23.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId24){
                        initLineChart(lineChart2, "sugar");
                        chip_24.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId25) {
                        initLineChart(lineChart2, "fat");
                        chip_25.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId26){
                        initLineChart(lineChart2, "saturatedfat");
                        chip_26.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId27){
                        initLineChart(lineChart2, "transfat");
                        chip_27.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId28){
                        initLineChart(lineChart2, "protein");
                        chip_28.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId29){
                        initLineChart(lineChart2, "cholesterol");
                        chip_29.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if (checkedId == chipId30){
                        initLineChart(lineChart2, "calcium");
                        chip_30.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }
        });

        return v;
    }

    private void initLineChart(LineChart lineChart, String component) {
        List<DailyInfo> dataList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date _7daysago = calendar.getTime();

        database = MainDB.getInstance(getContext());

        database.historyDao().getWeeklyDailyInfo(_7daysago, tomorrow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    {
                        dataList.clear();
                        dataList.addAll(item);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateFormat.setTimeZone(TimeZone.getDefault());
                        for (int i=0;i<=6;i++){
                            if(dataList.size() == 7){ break; }
                            if(dataList.size() == i){dataList.add(new DailyInfo(dateFormat.format(today)));}
                            Date dateChecker = calendar.getTime();
                            String checkDate = dateFormat.format(dateChecker);
                            if(!dataList.get(i).todayStr.equals(checkDate)){
                                Log.d("WeekDate","Before: "+checkDate + " / " + dataList.get(i).todayStr);
                                dataList.add(i,new DailyInfo(checkDate));
                                Log.d("WeekDate","After : "+checkDate + " / " + dataList.get(i).todayStr);
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                            }
                            else {
                                Log.d("WeekDate","None  : "+checkDate + " / " + dataList.get(i).todayStr);
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                continue;
                            }
                        }
                        Log.d("Week", dataList.size()+"");

                        List<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < dataList.size(); i++) {
                            if(component == "sodium"){ entries.add(new Entry(i, dataList.get(i).sodiumSum)); }
                            else if(component == "kcal"){ entries.add(new Entry(i, dataList.get(i).kcalSum)); }
                            else if(component == "carbonhydrate"){ entries.add(new Entry(i, dataList.get(i).carbonhydrateSum)); }
                            else if(component == "sugar"){ entries.add(new Entry(i, dataList.get(i).sugarSum)); }
                            else if(component == "fat"){ entries.add(new Entry(i, dataList.get(i).fatSum)); }
                            else if(component == "saturatedfat"){ entries.add(new Entry(i, dataList.get(i).saturatedfatSum)); }
                            else if(component == "transfat"){ entries.add(new Entry(i, dataList.get(i).transfatSum)); }
                            else if(component == "cholesterol"){ entries.add(new Entry(i, dataList.get(i).cholesterolSum)); }
                            else if(component == "protein"){ entries.add(new Entry(i, dataList.get(i).proteinSum)); }
                            else if(component == "calcium"){ entries.add(new Entry(i, dataList.get(i).calciumSum)); }
                            else { entries.add(new Entry(i,0)); }
                        }
                        Log.d("Week", component + " Called");
                        LineDataSet lineDataSet = new LineDataSet(entries, "주간 칼로리");
                        lineDataSet.setColor(getResources().getColor(R.color.primary)); // 선 색깔
                        lineDataSet.setCircleColor(Color.GRAY);   // 점 색깔
                        lineDataSet.setLineWidth(4f);   // 선 두께
                        lineDataSet.setCircleRadius(5f);    // 점 크기
                        lineDataSet.setValueTextSize(10f);  // 글자 크기
                        lineDataSet.setDrawHighlightIndicators(false);
                        lineDataSet.setDrawValues(true);    // 숫자 표시
                        lineDataSet.setDrawCircleHole(true);    // 점에 구멍 여부
                        lineDataSet.setValueTextColor(getResources().getColor(android.R.color.black));

                        LineData lineData = new LineData(lineDataSet);  // 꺾은선 그래프에 데이터 추가
                        lineChart.setData(lineData);
                        lineChart.getDescription().setEnabled(false);
                        lineChart.animateX(1000);
                        lineChart.getAxisRight().setEnabled(false); // y축 사용여부
                        lineChart.getAxisLeft().setEnabled(false);
                        lineChart.getLegend().setEnabled(false);    // legend 사용여부
                        lineChart.getDescription().setEnabled(false);   // 주석

                        // x축 레이블을 포맷팅하기 위한 ValueFormatter 클래스를 생성합니다.
                        WeekAxisValueFormatter xAxisFormatter = new WeekAxisValueFormatter();

                        // x축 설정
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setDrawLabels(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setTextSize(10f);
                        xAxis.setLabelRotationAngle(0f);
                        xAxis.setLabelCount(7, true);
                        xAxis.setValueFormatter(xAxisFormatter);

                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    }
                }, throwable -> {
                    Log.e("Errs","Error getting history data", throwable);});

    }

}