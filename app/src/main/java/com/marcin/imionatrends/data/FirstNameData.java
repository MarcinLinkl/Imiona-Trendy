package com.marcin.imionatrends.data;


public class FirstNameData {
    private int year;
    private String name;
    private int count;
    private int isMale;

    public FirstNameData(int year, String name, int count, int isMale) {
        this.year = year;
        this.name = name;
        this.count = count;
        this.isMale = isMale;
    }

    public int getYear() {
        return year;
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
