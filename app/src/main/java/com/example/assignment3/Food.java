package com.example.assignment3;

import java.math.BigDecimal;

public class Food {
    private Integer id;
    private String name;
    private String category;
    private Integer calorieAmount;
    private String servingUnit;
    private BigDecimal servingAmount;
    private Integer fat;

    public Food() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCalorieAmount() {
        return calorieAmount;
    }

    public void setCalorieAmount(Integer calorieAmount) {
        this.calorieAmount = calorieAmount;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public BigDecimal getServingAmount() {
        return servingAmount;
    }

    public void setServingAmount(BigDecimal servingAmount) {
        this.servingAmount = servingAmount;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }
}
