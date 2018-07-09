package com.foodie.swapnil.newdairy;

/**
 * Created by sumit on 6/14/2018.
 */

public class DairyModel {
    private String milk_Morn ;
    private String milk_Even ;
    private String date;
    private String id;

    public String getMilk_Morn() {
        return milk_Morn;
    }

    public void setMilk_Morn(String milk_Morn) {
        this.milk_Morn = milk_Morn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMilk_Even() {
        return milk_Even;
    }

    public void setMilk_Even(String milk_Even) {
        this.milk_Even = milk_Even;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
