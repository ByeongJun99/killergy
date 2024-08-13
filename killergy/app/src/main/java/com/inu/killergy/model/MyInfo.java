package com.inu.killergy.model;

import java.util.Date;
import java.util.List;

public class MyInfo {
    private final Float weight;
    private final Float height;
    private final Gender gender;
    private final List<String> allergyList;
    private final int myAge;

    private final Date birth;

    public MyInfo(Float weight, Float height, Gender gender, List<String> allergyList, int myAge, Date birth) {
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.allergyList = allergyList;
        this.myAge = myAge;
        this.birth = birth;
    }

    public Float getWeight() {
        return weight;
    }

    public Float getHeight() {
        return height;
    }

    public Gender getGender() {
        return gender;
    }

    public List<String> getAllergyList() {
        return allergyList;
    }

    public int getMyAge() { return myAge; }

    public Date getBirth() { return birth; }

}

