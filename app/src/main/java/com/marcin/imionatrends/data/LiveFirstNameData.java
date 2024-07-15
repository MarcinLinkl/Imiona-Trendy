package com.marcin.imionatrends.data;


public class LiveFirstNameData {
    private String name;
    private int count;
    private int isMale;

    public LiveFirstNameData(String name, int count, int isMale) {
        this.name = name;
        this.count = count;
        this.isMale = isMale;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int isMale() {
        return isMale;
    }
}
