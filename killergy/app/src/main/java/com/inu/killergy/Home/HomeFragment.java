package com.inu.killergy.Home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.MyInfo.MyInfoSettingFragment;
import com.inu.killergy.MyInfo.SharedPreferenceManger;
import com.inu.killergy.R;
import com.inu.killergy.model.Gender;
import com.inu.killergy.model.MyInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    MainDB database;

    private SharedPreferenceManger sm;
    private MyInfo myInfo;
    private String myGender;
    private Float myBMI;
    private Double requiredCal;
    private int myAge;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SharedPreferenceManger(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }


    public void updateProgress(int progress) {
        if(progress >= requiredCal*0.7){
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
        }
        progressBar.setProgress(progress);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();

        TextView textView0 = view.findViewById(R.id.txt_gender);
        textView0.setText(myGender);
        TextView textView1 = view.findViewById(R.id.txt_age);
        textView1.setText("만 "+ String.valueOf(myAge) +" 세");
        TextView textView2 = view.findViewById(R.id.txt_BMI);
        textView2.setText(String.format("%.2f", myBMI));
        TextView textView3 = view.findViewById(R.id.txt_Kcal);
        textView3.setText(String.valueOf((int)Math.round(requiredCal))+" kcal");
        TextView textView4 = view.findViewById(R.id.txt_MyBMI);
        textView4.setText(MyBmi(myBMI));

        progressBar = view.findViewById(R.id.progress_bar);
        TextView textView5 = view.findViewById(R.id.txt_progress);
        progressBar.setMax((int)Math.round(requiredCal));
        textView5.setText("일일 섭취량 : 0/"+String.valueOf((int)Math.round(requiredCal)));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        List<Float> dataList = new ArrayList<>();

        database = MainDB.getInstance(getContext());
        database.historyDao().getWeeklyKcal(today,tomorrow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item->{
                            if(item.size() != 0) {
                                dataList.clear();
                                dataList.addAll(item);
                                textView5.setText("일일 섭취량 : " + String.valueOf((int) Math.round(dataList.get(0))) + "/" + String.valueOf((int) Math.round(requiredCal)));
                                updateProgress((int) Math.round(dataList.get(0)));
                            } else {
                                updateProgress(0);
                            }
                        }, throwable -> {
                            Log.e("Errs","Error getting history data", throwable);}
                );


    }

    private MyInfo getMyBodyInfo(){
        String myInfoJson = sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE, MyInfoSettingFragment.MY_INFO_KEY);
        Gson gson = new Gson();
        return gson.fromJson(myInfoJson, MyInfo.class);
    }

    private void getData(){
        myInfo = getMyBodyInfo();
        Gender gender = myInfo.getGender();
        Float height = myInfo.getHeight();
        Float weight = myInfo.getWeight();
        myAge = myInfo.getMyAge();


        myBMI = weight/(height*height)*10000;
        if (gender == Gender.MALE){
            myGender = "남자";
            requiredCal = 662-(9.53*myAge)+1.2*(15.91*weight + 539.6*height/100);
        }
        else if (gender == Gender.FEMALE){
            myGender = "여자";
            requiredCal = 354-(6.91*myAge)+1.2*(9.36*weight + 726*height/100);
        }

    }


    private String MyBmi(Float BMI){
        if (BMI < 18.5) {
            return "저체중";
        } else if (BMI >= 18.5 && BMI < 23) {
            return "정상";
        } else if (BMI >= 23 && BMI < 25) {
            return "비만 전 단계";
        } else if (BMI >= 25 && BMI < 30){
            return "1단계 비만";
        } else if (BMI >= 30 && BMI < 35){
            return "2단계 비만";
        } else {
            return "3단계 비만";
        }
    }

}