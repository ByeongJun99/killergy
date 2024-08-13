package com.inu.killergy.DB;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;


@Entity
public class History implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    /*제품명*/
    public String productName;
    /*날짜*/
    public Date searchDate;
    /*알러지*/
    public String allergies;
    /*같은 제조시설*/
    public String sameFactory;
    /*칼로리*/
    public float kcalAmount;
    /*나트륨*/
    public float sodiumContent;
    /*탄수화물*/
    public float carbonhydrateContent;
    /*당류*/
    public float sugarContent;
    /*지방*/
    public float fatContent;
    /*트랜스지방*/
    public float transfatContent;
    /*포화지방*/
    public float saturatedfatContent;
    /*콜레스테롤*/
    public float cholesterolContent;
    /*단백질*/
    public float proteinContent;
    /*칼슘*/
    public float calciumContent;
    /*이미지*/
    public byte[] productImage;

    public String Componentstr(){
        StringBuffer sb = new StringBuffer();
        sb.append("열량 : " + kcalAmount);
        sb.append("나트륨: " + sodiumContent);
        sb.append(", 탄수화물: " + carbonhydrateContent);
        sb.append(", 당류: " + sugarContent);
        sb.append(", 지방: " + fatContent);
        sb.append(", 트랜스지방: " + transfatContent);
        sb.append(", 포화지방: " + saturatedfatContent);
        sb.append(", 콜레스테롤: " + cholesterolContent);
        sb.append(", 단백질: " + proteinContent);
        return sb.toString();
    }
    public void setContent(float kcal, float sodium, float cbhyd, float sugar, float fat,
                           float trnsfat, float satfat, float chol, float protein, float calcium){
        this.kcalAmount = kcal;
        this.sodiumContent = sodium;
        this.carbonhydrateContent = cbhyd;
        this.sugarContent = sugar;
        this.fatContent = fat;
        this.transfatContent = trnsfat;
        this.saturatedfatContent = satfat;
        this.cholesterolContent = chol;
        this.proteinContent = protein;
        this.calciumContent = calcium;
    }
}
