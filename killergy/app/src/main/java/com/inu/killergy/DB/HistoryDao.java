package com.inu.killergy.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    Flowable<List<History>> getAllHistory();

    @Query("SELECT * FROM history WHERE searchDate BETWEEN :date1 AND :date2 ORDER BY id DESC")
    Flowable<List<History>> getBetweenDateHistory(Date date1, Date date2);

    @Query("SELECT SUM(kcalAmount) " +
            "FROM history " +
            "WHERE searchDate BETWEEN :aWeekAgo AND :today " +
            "GROUP BY strftime('%Y-%m-%d', datetime(searchDate / 1000, 'unixepoch', 'localtime'))")
    Flowable<List<Float>> getWeeklyKcal(Date aWeekAgo, Date today);


    @Query("SELECT strftime('%Y-%m-%d', searchDate / 1000, 'unixepoch', 'localtime') as todayStr," +
            "SUM(kcalAmount) as kcalSum, SUM(sodiumContent) as sodiumSum, " +
            "SUM(carbonhydrateContent) as carbonhydrateSum, SUM(sugarContent) as sugarSum," +
            "SUM(fatContent) as fatSum, SUM(transfatContent) as transfatSum, " +
            "SUM(saturatedfatContent) as saturatedfatSum, SUM(cholesterolContent) as cholesterolSum, " +
            "SUM(proteinContent) as proteinSum, SUM(calciumContent) as calciumSum " +
            "FROM history " +
            "WHERE searchDate BETWEEN :aWeekAgo AND :today " +
            "GROUP BY strftime('%Y-%m-%d', searchDate / 1000, 'unixepoch', 'localtime')")
    Flowable<List<DailyInfo>> getWeeklyDailyInfo(Date aWeekAgo, Date today);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertHistory(History... history);

    @Delete
    Completable deleteHistory(History... history);

}
