package com.inu.killergy;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inu.killergy.Analyze.ComponentInfoFragment;
import com.inu.killergy.History.HistoryFragment;
import com.inu.killergy.Home.HomeFragment;
import com.inu.killergy.MyInfo.MyInfoFragment;
import com.inu.killergy.MyInfo.MyInfoSettingFragment;
import com.inu.killergy.MyInfo.SharedPreferenceManger;
import com.inu.killergy.Notification.NotificationActivity;
import com.inu.killergy.WeeklyReport.WeeklyAnalysisFragment;

public class MainActivity extends AppCompatActivity {

    private SharedPreferenceManger sm;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeFragment fragmentHome = new HomeFragment();
    private WeeklyAnalysisFragment fragmentWeeklyAnalysis = new WeeklyAnalysisFragment();
    private ComponentInfoFragment fragmentComponentInfo = new ComponentInfoFragment();
    private HistoryFragment fragmentHistory = new HistoryFragment();
    private MyInfoSettingFragment fragmentMyInfoSetting = new MyInfoSettingFragment();
    private MyInfoFragment fragmentMyInfo = new MyInfoFragment();

    private final String DEFAULT = "DEFAULT";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentMyInfoSetting).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        sm = new SharedPreferenceManger(getBaseContext());

        fragmentMyInfoSetting.setHasOptionsMenu(true);

        ImageButton prfBtn = (ImageButton) findViewById(R.id.preference_btn1);

        prfBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                view.startAnimation(anim);

                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });

        createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);

        Intent intent = new Intent(this, MainActivity.class);       // 클릭시 실행할 activity를 지정
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        SharedPreferences sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean switchState = sharedPref.getBoolean("switch_state", false);

        bottomNavigationView.setSelectedItemId(R.id.Tab_5);
    }

    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "엡을 종료하려면 \'뒤로' 버튼을 한 번 더 누르세요.", Toast.LENGTH_SHORT).show();
        }
        else if (System.currentTimeMillis() <= backPressedTime + 2000) {
            finish();
        }
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.Tab_1:
                    transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.Tab_2:
                    transaction.replace(R.id.menu_frame_layout, fragmentWeeklyAnalysis).commitAllowingStateLoss();
                    break;
                case R.id.Tab_3:
                    transaction.replace(R.id.menu_frame_layout, fragmentComponentInfo).commitAllowingStateLoss();
                    break;
                case R.id.Tab_4:
                    transaction.replace(R.id.menu_frame_layout, fragmentHistory).commitAllowingStateLoss();
                    break;
                case R.id.Tab_5:
                    Fragment selectFragment;
                    if(sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE,MyInfoSettingFragment.MY_INFO_KEY).isEmpty())
                        selectFragment = fragmentMyInfoSetting;
                    else
                        selectFragment = fragmentMyInfo;
                    transaction.replace(R.id.menu_frame_layout, selectFragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    void createNotificationChannel(String channelId, String channelName, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    public void createNotification(String channelId, int id, String title, String text, Intent intent)
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.medicine)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)    // 클릭시 설정된 PendingIntent가 실행된다
                .setAutoCancel(true)                // true이면 클릭시 알림이 삭제된다
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    void destroyNotification(int id)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}