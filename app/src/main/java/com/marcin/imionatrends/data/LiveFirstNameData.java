package com.marcin.imionatrends.data;


public class LiveFirstNameData {
    private String orderID;
    private String name;
    private int count;
    private int isMale;
    private float percentage;

    public LiveFirstNameData(String name, int count, int isMale, float percentage) {
        this.name = name;
        this.count = count;
        this.isMale = isMale;
        this.percentage = percentage;
    }
    public LiveFirstNameData(String name, int count, int isMale) {
        this.name = name;
        this.count = count;
        this.isMale = isMale;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }



    public int getIsMale() {
        return isMale;
    }



    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }


}
