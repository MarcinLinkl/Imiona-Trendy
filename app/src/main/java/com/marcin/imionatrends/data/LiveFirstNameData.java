package com.marcin.imionatrends.data;


public class LiveFirstNameData {
    private String name;
    private int count;
    private boolean isMale;

    public LiveFirstNameData(String name, int count, boolean isMale) {
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

    public boolean isMale() {
        return isMale;
    }
}
