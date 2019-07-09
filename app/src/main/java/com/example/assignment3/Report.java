package com.example.assignment3;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Report {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "userId")
    private Integer userId;

    @ColumnInfo(name = "time")
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @ColumnInfo(name = "totalCaloriesConsumed")
    private Integer totalCaloriesConsumed;

    @ColumnInfo(name = "totalCaloriesBurned")
    private Integer totalCaloriesBurned;

    @ColumnInfo(name = "stepsTaken")
    private Integer stepsTaken;

    @ColumnInfo(name = "calorieGoal")
    private Integer calorieGoal;

    public Report(){
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(Integer totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public Integer getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(Integer totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public Integer getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(Integer totalStepsTaken) {
        this.stepsTaken = totalStepsTaken;
    }

    public Integer getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(Integer calorieGoal) {
        this.calorieGoal = calorieGoal;
    }
}
