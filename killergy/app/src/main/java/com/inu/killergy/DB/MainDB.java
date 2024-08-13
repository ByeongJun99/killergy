package com.inu.killergy.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {History.class, Notification.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MainDB extends RoomDatabase {
    public abstract HistoryDao historyDao();
    public abstract NotificationDao notificationDao();

    private static MainDB INSTANCE;

    public static MainDB getInstance(Context context){
        synchronized (MainDB.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MainDB.class, "mainDatabase.db")
                        .fallbackToDestructiveMigration()
                        .addCallback(new RoomDatabase.Callback(){
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db){
                                super.onCreate(db);
                            }
                        })
                        .build();
                }
            }
        return INSTANCE;
    }
}
