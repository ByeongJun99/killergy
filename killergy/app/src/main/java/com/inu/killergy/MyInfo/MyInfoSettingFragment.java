package com.inu.killergy.MyInfo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.inu.killergy.MainActivity;
import com.inu.killergy.R;
import com.inu.killergy.model.Gender;
import com.inu.killergy.model.MyInfo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.TimeZone;


public class MyInfoSettingFragment extends Fragment {
    public static final String MY_INFO_KEY = "my_info";
    private SharedPreferenceManger sm;

    private View view;
    private EditText weightEditText;
    private EditText heightEditText;

    private Button birth_button;
    private Button saveButton;

    private Date birth;
    private int myAge;
    private RadioGroup genderRadioGroup;

    private ChipGroup allergyGroup;

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
        View rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_info_setting, container, false);
        birth_button = rootView.findViewById(R.id.birth_button);

        if (sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE,MyInfoSettingFragment.MY_INFO_KEY).isEmpty()){
            Calendar calendar = Calendar.getInstance();
            Date currentDate = Calendar.getInstance().getTime();
            birth_button.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentDate));
            DatePickerDialog.OnDateSetListener dpDialog = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONDAY, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    birth = calendar.getTime();
                    updateDate();
                }

                private void updateDate() {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd", Locale.KOREA);
                    birth_button = rootView.findViewById(R.id.birth_button);
                    birth_button.setText(sdf.format(calendar.getTime()));
                    myAge = calAge();
                }
            };

            birth_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar, dpDialog,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }
        else{

            SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd", Locale.KOREA);
            MyInfo myInfo = getMyInfo();
            Date my_birth = myInfo.getBirth();
            birth_button.setText(sdf.format(my_birth));
            birth = my_birth;
            Calendar calendar = Calendar.getInstance();
            myAge = calAge();
            DatePickerDialog.OnDateSetListener dpDialog = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONDAY, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    birth = calendar.getTime();
                    updateDate();

                }

                private void updateDate() {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd", Locale.KOREA);
                    birth_button = rootView.findViewById(R.id.birth_button);
                    birth_button.setText(sdf.format(calendar.getTime()));
                    myAge = calAge();
                }
            };

            birth_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar.setTime(birth);
                    new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar, dpDialog,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }


        Chip[] chips = new Chip[18];

        for (int i = 0; i < 18; i++) {
            int chipId = getResources().getIdentifier("chip" + (i+1), "id", getActivity().getPackageName());
            chips[i] = rootView.findViewById(chipId);
            chips[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int chipIndex = Arrays.asList(chips).indexOf(buttonView);
                    SharedPreferences prefs = getActivity().getSharedPreferences("ChipsState", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("chip" + (chipIndex + 1), isChecked);
                    editor.apply();
                }
            });

            SharedPreferences prefs = getActivity().getSharedPreferences("ChipsState", Context.MODE_PRIVATE);
            boolean isChecked = prefs.getBoolean("chip" + (i+1), false);
            chips[i].setChecked(isChecked);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navigationView = getActivity().findViewById(R.id.menu_bottom_navigation);
        Menu menu = navigationView.getMenu();
        HomeItem = menu.findItem(R.id.Tab_1);
        WeekItem = menu.findItem(R.id.Tab_2);
        AnalyzeItem = menu.findItem(R.id.Tab_3);
        HistoryItem = menu.findItem(R.id.Tab_4);
        if(sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE,MyInfoSettingFragment.MY_INFO_KEY).isEmpty()){
            HomeItem.setEnabled(false);
            WeekItem.setEnabled(false);
            AnalyzeItem.setEnabled(false);
            HistoryItem.setEnabled(false);
        }
        else{
            MyInfo myInfo = getMyInfo();
            Gender gender = myInfo.getGender();
            List<String> allergyList = myInfo.getAllergyList();
            Float height = myInfo.getHeight();
            Float weight = myInfo.getWeight();

            EditText editText0 = view.findViewById(R.id.weightEditTextView);
            editText0.setText(weight.toString());
            EditText editText1 = view.findViewById(R.id.heightEditTextView);
            editText1.setText(height.toString());
            if (gender == Gender.MALE){
                RadioButton radioButton1 = view.findViewById(R.id.radioButton1);
                radioButton1.setChecked(true);
            }
            else if (gender == Gender.FEMALE){
                RadioButton radioButton2 = view.findViewById(R.id.radioButton2);
                radioButton2.setChecked(true);
            }

            HomeItem.setEnabled(true);
            WeekItem.setEnabled(true);
            AnalyzeItem.setEnabled(true);
            HistoryItem.setEnabled(true);
        }

        weightEditText = view.findViewById(R.id.weightEditTextView);
        heightEditText = view.findViewById(R.id.heightEditTextView);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        allergyGroup = view.findViewById(R.id.allergyGroup);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveMyInfo());
        this.view = view;
    }

    private void saveMyInfo(){

        if(!validationWeight() || !validationHeight() || !validationGender()){
            showToast("몸무게, 키, 성별을 모두 입력하세요.");
            return;
        }
        else if(!validationAge()){
            showToast("생년월일을 다시 입력하세요.");
            return;
        }


        Float weight = Float.valueOf(weightEditText.getText().toString());
        Float height = Float.valueOf(heightEditText.getText().toString());
        Gender gender = getGender();
        List<String> allergyList = getAllergyList();
        MyInfo myInfo = new MyInfo(weight,height,gender,allergyList, myAge, birth);
        Gson gson = new Gson();
        String myInfoJson = gson.toJson(myInfo);
        sm.saveString(SharedPreferenceManger.MY_INFO_PREFERENCE,MY_INFO_KEY,myInfoJson);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_frame_layout,new MyInfoFragment());
        transaction.commit();


    }

    private boolean validationWeight(){
        if(TextUtils.isEmpty(weightEditText.getText().toString())){
            return false;
        }
        return true;
    }

    private boolean validationHeight(){
        if(TextUtils.isEmpty(heightEditText.getText().toString())){
            return false;
        }
        return true;
    }

    private boolean validationGender(){
        int checkedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        if(checkedRadioButtonId == -1){
            return false;
        }
        return true;
    }

    private boolean validationAge(){
        if(myAge==-1){
            return false;
        }
        return true;
    }

    private int calAge(){
        Calendar now_calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar birth_calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date today = new Date();
        now_calendar.setTime(today);
        birth_calendar.setTime(birth);

        int Age = now_calendar.get(Calendar.YEAR) - birth_calendar.get(Calendar.YEAR);
        if(now_calendar.get(Calendar.YEAR) < birth_calendar.get(Calendar.YEAR)){
            return -1;
        }
        else if(now_calendar.get(Calendar.YEAR) == birth_calendar.get(Calendar.YEAR)){
            if(now_calendar.get(Calendar.MONTH) < birth_calendar.get(Calendar.MONTH)){
                return -1;
            }
            else if(now_calendar.get(Calendar.MONTH) == birth_calendar.get(Calendar.MONTH)){
                if(now_calendar.get(Calendar.DAY_OF_MONTH) <= birth_calendar.get(Calendar.DAY_OF_MONTH)){
                    return -1;
                }
                else return Age;
            }
            else {
                return Age;
            }
        }
        else {
            if(now_calendar.get(Calendar.MONTH) < birth_calendar.get(Calendar.MONTH)){
                Age -= 1;

            }
            else if(now_calendar.get(Calendar.DAY_OF_MONTH) < birth_calendar.get(Calendar.DAY_OF_MONTH)){
                Age -= 1;

            }
            return Age;
        }
    }

    private Gender getGender(){
        RadioButton genderRadio = view.findViewById(genderRadioGroup.getCheckedRadioButtonId());
        if(genderRadio.getText().equals("남자"))
            return Gender.MALE;
        else
            return Gender.FEMALE;
    }

    private List<String> getAllergyList(){
        return allergyGroup.getCheckedChipIds()
                .stream().map(id -> ((Chip)view.findViewById(id)).getText().toString())
                .collect(Collectors.toList());
    }

    public MyInfo getMyInfo(){
        String myInfoJson = sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE, MyInfoSettingFragment.MY_INFO_KEY);
        Gson gson = new Gson();
        return gson.fromJson(myInfoJson, MyInfo.class);
    }

    private void showToast(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }
}