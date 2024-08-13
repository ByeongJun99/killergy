package com.inu.killergy.MyInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.inu.killergy.R;
import com.inu.killergy.model.Gender;
import com.inu.killergy.model.MyInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyInfoFragment extends Fragment {
    private SharedPreferenceManger sm;
    private Button modifyButton;

    private String myGender;
    private MenuItem HomeItem;
    private MenuItem WeekItem;
    private MenuItem AnalyzeItem;
    private MenuItem HistoryItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SharedPreferenceManger(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_info, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyInfo myInfo = getMyInfo();
        Gender gender = myInfo.getGender();
        List<String> allergyList = myInfo.getAllergyList();
        Float height = myInfo.getHeight();
        Float weight = myInfo.getWeight();
        Date birth = myInfo.getBirth();

        if (gender == Gender.MALE){
            myGender = "남자";
        }
        else if (gender == Gender.FEMALE){
            myGender = "여자";
        }

        modifyButton = view.findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(v -> modifyMyInfo());
        TextView textView0 = view.findViewById(R.id.gender);
        textView0.setText(myGender);
        TextView textView1 = view.findViewById(R.id.height);
        textView1.setText(height.toString()+" cm");
        TextView textView2 = view.findViewById(R.id.weight);
        textView2.setText(weight.toString()+" kg");
        TextView textView3 = view.findViewById(R.id.allergy);
        textView3.setText(allergyList.toString());

        BottomNavigationView navigationView = getActivity().findViewById(R.id.menu_bottom_navigation);
        Menu menu = navigationView.getMenu();
        HomeItem = menu.findItem(R.id.Tab_1);
        WeekItem = menu.findItem(R.id.Tab_2);
        AnalyzeItem = menu.findItem(R.id.Tab_3);
        HistoryItem = menu.findItem(R.id.Tab_4);
        HomeItem.setEnabled(true);
        WeekItem.setEnabled(true);
        AnalyzeItem.setEnabled(true);
        HistoryItem.setEnabled(true);



    }

    public MyInfo getMyInfo(){
        String myInfoJson = sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE, MyInfoSettingFragment.MY_INFO_KEY);
        Gson gson = new Gson();
        return gson.fromJson(myInfoJson, MyInfo.class);
    }

    private void modifyMyInfo(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_frame_layout,new MyInfoSettingFragment());
        transaction.commit();
    }
}