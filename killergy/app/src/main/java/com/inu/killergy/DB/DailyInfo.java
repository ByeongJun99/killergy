package com.inu.killergy.DB;

public class DailyInfo {
    public String todayStr;
    public float kcalSum;
    public float sodiumSum;
    public float carbonhydrateSum;
    public float sugarSum;
    public float fatSum;
    public float transfatSum;
    public float saturatedfatSum;
    public float cholesterolSum;
    public float proteinSum;
    public float calciumSum;
    public DailyInfo(String todayStr){
        this.todayStr = todayStr;
        this.kcalSum = 0f;
        this.sodiumSum = 0f;
        this.carbonhydrateSum = 0f;
        this.sugarSum = 0f;
        this.fatSum = 0f;
        this.transfatSum = 0f;
        this.saturatedfatSum = 0f;
        this.cholesterolSum = 0f;
        this.proteinSum = 0f;
        this.calciumSum = 0f;
    }
}
