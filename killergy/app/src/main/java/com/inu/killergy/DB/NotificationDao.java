package com.inu.killergy.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notification ORDER BY id DESC")
    Flowable<List<Notification>> getAllNotification();

    @Query("DELETE FROM notification")
    Completable deleteAllNotification();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertNotification(Notification... notification);

    @Delete
    Completable deleteNotification(Notification... notification);
}
