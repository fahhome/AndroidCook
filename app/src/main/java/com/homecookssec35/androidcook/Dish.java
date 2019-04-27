package com.homecookssec35.androidcook;

public class Dish {

    private int id, cookId ;
    private String nameOfDish, description,isActive , estimatedtime, productCode;
    private double  unitPrice ;
    private boolean active ;

    public int getId() {
        return id;
    }

    public String getEstimatedtime() {
        return estimatedtime;
    }

    public void setEstimatedtime(String estimatedtime) {
        this.estimatedtime = estimatedtime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCookId() {
        return cookId;
    }

    public void setCookId(int cookId) {
        this.cookId = cookId;
    }

    public String getNameOfDish() {
        return nameOfDish;
    }

    public void setNameOfDish(String nameOfDish) {
        this.nameOfDish = nameOfDish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Dish(int id, int cookId, String nameOfDish, String description,  double unitPrice, boolean active,String productCode, String esttime) {
        this.id = id;
        this.cookId = cookId;
        this.nameOfDish = nameOfDish;
        this.description = description;
        this.isActive = isActive;
        this.unitPrice = unitPrice;
        this.productCode = productCode;
        this.estimatedtime = esttime ;
    }
}
