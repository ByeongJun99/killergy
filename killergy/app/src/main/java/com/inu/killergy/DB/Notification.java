package com.inu.killergy.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Notification implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    /*텍스트*/
    public String text;
    /*날짜*/
    public Date date;
}
