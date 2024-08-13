package com.inu.killergy.History;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.inu.killergy.DB.History;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;
    List<History> dataList = new ArrayList<>();
    MainDB database;
    HistoryAdapter adapter;

    Calendar calendar;
    Date date1, date2, date3;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        Button historyBtn = view.findViewById(R.id.historyBtn);
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Long today = MaterialDatePicker.todayInUtcMilliseconds();
        date1 = new Date();
        date2 = new Date();
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setValidator(DateValidatorPointBackward.now());
                constraintsBuilder.setEnd(calendar.getTimeInMillis());
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setCalendarConstraints(constraintsBuilder.build());
                builder.setTitleText("기간을 선택하세요.");

                builder.setSelection(Pair.create(today-6 * 24 * 60 * 60 * 1000, today));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);
                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.setTime(date2);
                        newCalendar.add(Calendar.DATE, 1);
                        date3 = newCalendar.getTime();
                        String dateString1 = simpleDateFormat.format(date1);
                        String dateString2 = simpleDateFormat.format(date2);

                        historyBtn.setText(dateString1 + " ~ " + dateString2);
                        CallDB();
                    }
                });
            }
        });

        CallDB();

        return view;
    }

    public void CallDB(){

        database = MainDB.getInstance(getContext());
        database.historyDao().getBetweenDateHistory(date1, date3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    dataList.clear();
                    dataList.addAll(item);
                    adapter = new HistoryAdapter(getActivity(), dataList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }, throwable -> {
                    Log.e("Errs","Error getting history data", throwable);});
    }
}