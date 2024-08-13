package com.inu.killergy.Notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inu.killergy.DB.MainDB;
import com.inu.killergy.DB.Notification;
import com.inu.killergy.History.HistoryAdapter;
import com.inu.killergy.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {

    private Switch switchView;
    private SharedPreferences sharedPref;
    private notificationAdapter mAdapter;
    private MainDB database;
    RecyclerView recyclerView;
    private List<Notification> notificationList = new ArrayList<>();
    private RecyclerView notificationRecyclerView;
    private notificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView = findViewById(R.id.notification_recycler_view);

        switchView = findViewById(R.id.switch1);
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);

        // SharedPreferences에서 switch 상태 불러오기
        boolean switchState = sharedPref.getBoolean("switch_state", false);
        switchView.setChecked(switchState);

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                v.startAnimation(anim);
                finish(); // 현재 액티비티를 종료하고 이전 액티비티로 돌아가기
            }
        });

        // 스위치 상태가 변경될 때 SharedPreferences에 저장하기
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("switch_state", isChecked);
                editor.apply();
            }
        });

        database = MainDB.getInstance(this);

        database.notificationDao().getAllNotification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    notificationList.clear();
                    notificationList.addAll(item);
                    mAdapter = new notificationAdapter(this, notificationList);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                }, throwable -> {
                    Log.e("Errs","Error getting history data", throwable);});

        Button allDeleteBtn = findViewById(R.id.allDeleteBtn);
        allDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.notificationDao().deleteAllNotification()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            // 성공적으로 모든 알림을 삭제했을 때 실행되는 코드
                        }, throwable -> {
                            // 삭제 중 에러가 발생했을 때 실행되는 코드
                        });
            }
        });
    }

}