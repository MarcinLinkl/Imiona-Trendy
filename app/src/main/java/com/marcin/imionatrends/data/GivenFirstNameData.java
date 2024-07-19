package com.marcin.imionatrends.data;


public class GivenFirstNameData {
    private int year;
    private String name;
    private int count;
    private int isMale;
    private double percentage;

    public GivenFirstNameData(String name, int count, int isMale, double percentage) {

        this.name = name;
        this.count = count;
        this.isMale = isMale;
        this.percentage = percentage;
    }
    public GivenFirstNameData(String name, int count, double percentage) {

        this.name = name;
        this.count = count;
        this.percentage = percentage;
    }
    public GivenFirstNameData(int year, String name, int count, int isMale) {
        this.year = year;
        this.name = name;
        this.count = count;
        this.isMale = isMale;
        this.percentage = percentage;
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
